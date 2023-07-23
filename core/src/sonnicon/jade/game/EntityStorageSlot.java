package sonnicon.jade.game;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.storage.EntitySizeComponent;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.actors.InventorySlotButton;
import sonnicon.jade.util.DoubleLinkedList;
import sonnicon.jade.util.Events;
import sonnicon.jade.util.Function3;

import java.util.function.Consumer;

public class EntityStorageSlot {
    // General data
    private Entity entity;
    private int amount;
    private EntityStorage storage;
    private DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node;

    // Restrictions
    public EntitySize minimumSize = EntitySize.tiny;
    public EntitySize maximumSize = EntitySize.huge;
    public int maximumAmount = Integer.MAX_VALUE;
    public Function3<EntityStorageSlot, Entity, Integer, Integer> restriction = null;

    // Events
    private Events<EntityStorageChange> events;

    public EntityStorageSlot() {
    }

    public EntityStorageSlot(Entity entity, int amount, EntityStorage storage, DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node) {
        this.storage = storage;
        this.node = node;
        if (entity != null && amount > 0) {
            add(entity, amount);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public int getAmount() {
        return amount;
    }

    public EntityStorage getStorage() {
        return storage;
    }

    public DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> getNode() {
        return node;
    }

    public boolean isSelected() {
        return InventorySlotButton.selectedStorageSlot == this;
    }

    public boolean isEmpty() {
        return entity == null || amount <= 0;
    }

    public int add(Entity entity, int amount) {
        return add(entity, amount, false);
    }

    protected int add(Entity entity, int amount, boolean force) {
        if (!force) {
            // Slot already contains something else
            if (!isEmpty() && !this.entity.compare(entity)) {
                return 0;
            }

            // Storage restrictions
            if ((amount = storage.getMaxAddAmount(entity, amount)) <= 0) {
                return 0;
            }

            // Slot size restriction
            if (!EntitySizeComponent.fitsSize(entity, minimumSize, maximumSize)) {
                return 0;
            }

            // Slot amount restriction
            if ((amount = Math.min(amount, maximumAmount - this.amount)) <= 0) {
                return 0;
            }

            // Slot function restriction
            if (restriction != null && (amount = restriction.apply(this, entity, amount)) <= 0) {
                return 0;
            }
        }

        if (isEmpty()) {
            this.entity = entity;
            this.amount = amount;
            handleEvent(EntityStorageChange.set);
        } else {
            this.amount += amount;
            handleEvent(EntityStorageChange.add, this, amount);
        }

        storage.onSlotChanged(this, entity, this.amount - amount, amount);
        return amount;
    }

    public int remove(int amount) {
        if (amount <= 0) {
            return 0;
        }
        // removing everything
        if (amount >= this.amount) {
            amount = this.amount;
            empty();
            return amount;
        } else {
            this.amount -= amount;
            handleEvent(EntityStorageChange.remove, this, amount);
        }
        storage.onSlotChanged(this, entity, this.amount + amount, amount);
        return amount;
    }

    public int moveToStorage(EntityStorageSlot destination, int amount) {
        if (destination == null || destination.isEmpty()) {
            return 0;
        }
        amount = amount < 0 ? 0 : Math.min(amount, this.amount);

        // Split one and work on that
        if (destination.amount > 1) {
            EntityStorageSlot newDestination = destination.storage.addToNewSlot(destination.entity.copy(), 1);
            if (newDestination == null) {
                return 0;
            } else {
                destination.remove(1);
                destination = newDestination;
            }
        }

        StorageComponent storageComponent = destination.getEntity().getComponent(StorageComponent.class);
        int transfer = storageComponent.storage.addEntityAmount(getEntity().copy(), amount);
        return remove(transfer);
    }

    public int moveAll(EntityStorageSlot destination) {
        return moveAll(destination, null);
    }

    public int moveAll(EntityStorageSlot destination, InventoryMove action) {
        return move(destination, getAmount(), action);
    }

    public int move(EntityStorageSlot destination, int amount, InventoryMove action) {
        if (destination == null || amount <= 0) {
            return 0;
        }

        // Only move into empty slots
        if (action != InventoryMove.cancel && destination.isEmpty()) {
            action = InventoryMove.move;
        }

        // Auto-detect action if null
        if (action == null) {
            action = matchesEntity(destination) ? InventoryMove.move : InventoryMove.cancel;
            if (destination.hasStorageEntity()) {
                action = action == InventoryMove.cancel ? InventoryMove.insert : InventoryMove.cancel;
            }
        }

        if (action == InventoryMove.cancel) {
            return 0;
        }

        switch (action) {
            case insert:
                return moveToStorage(destination, amount);
            case move:
                return remove(destination.add(entity.copy(), Math.min(amount, this.amount)));
        }
        return 0;
    }

    public boolean matchesEntity(EntityStorageSlot other) {
        return !isEmpty() && !other.isEmpty() && getEntity().compare(other.getEntity());
    }

    public boolean hasStorageEntity() {
        return !isEmpty() && getEntity().hasComponent(StorageComponent.class);
    }

    public boolean exists() {
        return storage != null;
    }

    public void empty() {
        storage.onSlotChanged(this, entity, amount, amount = 0);
        entity = null;
        handleEvent(EntityStorageChange.clear, this);
    }

    public void attach(EntityStorage storage, DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node) {
        if (exists()) {
            disconnect();
        }

        this.storage = storage;
        this.node = node;

        storage.onSlotChanged(this, entity, 0, amount);
        handleEvent(EntityStorageChange.attach, this);
    }

    protected void disconnect() {
        storage.onSlotDisconnected(this);
        handleEvent(EntityStorageChange.detach);
        storage.slots.removeNode(node);
        this.storage = null;
        this.node = null;
    }

    public void registerEvent(EntityStorageChange key, Consumer<Object[]> handler) {
        // Lazy create events when we need them
        if (events == null) {
            events = new Events<>();
        }
        events.register(key, handler);
    }

    public void unregisterEvent(EntityStorageChange key, Consumer<Object[]> handler) {
        if (events != null) {
            events.unregister(key, handler);
        }
    }

    protected void handleEvent(EntityStorageChange key, Object... values) {
        if (events != null) {
            events.handle(key, values);
        }
    }

    public EntityStorageSlot copy() {
        // we don't keep connections or events
        EntityStorageSlot slot = new EntityStorageSlot();
        slot.entity = entity.copy();
        slot.amount = amount;

        slot.minimumSize = minimumSize;
        slot.maximumSize = maximumSize;
        slot.maximumAmount = maximumAmount;
        return slot;
    }

    public boolean compare(EntityStorageSlot other) {
        return (this == other) || (
                other != null &&
                        amount == other.amount &&
                        (entity == other.entity || (entity != null && entity.compare(other.entity))));
    }

    public enum InventoryMove {
        move("icon-insert-slot"),
        insert("icon-insert-storage"),
        cancel;

        public final Drawable icon;

        InventoryMove() {
            this.icon = null;
        }

        InventoryMove(String key) {
            this.icon = Textures.atlasFindDrawable(key);
        }
    }

    public enum EntityStorageChange {
        set,
        add,
        remove,
        clear,
        attach,
        detach
    }
}
