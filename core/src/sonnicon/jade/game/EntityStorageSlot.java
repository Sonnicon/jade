package sonnicon.jade.game;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.storage.EntitySizeComponent;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.actors.InventorySlotButton;
import sonnicon.jade.util.*;

import java.util.Map;

@EventGenerator(id = "StorageSlotSet", param = {EntityStorageSlot.class}, label = {"slot"})
@EventGenerator(id = "StorageSlotAdd", param = {EntityStorageSlot.class, Integer.class}, label = {"slot", "amount"})
@EventGenerator(id = "StorageSlotRemove", param = {EntityStorageSlot.class, Integer.class}, label = {"slot", "amount"})
@EventGenerator(id = "StorageSlotClear", param = {EntityStorageSlot.class}, label = {"slot"})
@EventGenerator(id = "StorageSlotAttach", param = {EntityStorageSlot.class}, label = {"slot"})
@EventGenerator(id = "StorageSlotDetach", param = {EntityStorageSlot.class}, label = {"slot"})
public class EntityStorageSlot implements ICopyable, IComparable, IDebuggable {
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
    public final Events events = new Events();

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
            EventTypes.StorageSlotSetEvent.handle(events, this);
        } else {
            this.amount += amount;
            EventTypes.StorageSlotAddEvent.handle(events, this, amount);
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
            EventTypes.StorageSlotRemoveEvent.handle(events, this, amount);
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
        EventTypes.StorageSlotClearEvent.handle(events, this);
    }

    public void attach(EntityStorage storage, DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node) {
        if (exists()) {
            disconnect();
        }

        this.storage = storage;
        this.node = node;

        storage.onSlotChanged(this, entity, 0, amount);
        EventTypes.StorageSlotAttachEvent.handle(events, this);
    }

    protected void disconnect() {
        storage.onSlotDisconnected(this);
        EventTypes.StorageSlotDetachEvent.handle(events, this);
        storage.slots.removeNode(node);
        this.storage = null;
        this.node = null;
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

    @Override
    public boolean compare(IComparable o) {
        if (!(o instanceof EntityStorageSlot)) {
            return false;
        }
        EntityStorageSlot other = (EntityStorageSlot) o;
        return this == other || amount == other.amount && (entity == other.entity || entity != null && entity.compare(other.entity));
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("entity", entity, "amount", amount,
                "minSize", minimumSize, "maxSize", maximumSize,
                "maxAmount", maximumAmount, "restricted", restriction != null);
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
}
