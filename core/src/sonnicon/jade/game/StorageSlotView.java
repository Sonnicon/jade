package sonnicon.jade.game;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.actors.InventorySlotButton;
import sonnicon.jade.util.DoubleLinkedList;

public class StorageSlotView {
    protected EntityStorage storage;
    protected DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node;

    public StorageSlotView(EntityStorage storage, DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node) {
        this.storage = storage;
        this.node = node;
    }

    public boolean isSelected() {
        return equals(InventorySlotButton.selectedStorageSlot);
    }

    public boolean hasStack() {
        return node.value != null;
    }

    public EntityStorage.EntityStack getStack() {
        return node.value;
    }

    public EntityStorage getStorage() {
        return storage;
    }

    public int moveToSlot(StorageSlotView destination) {
        int transfer = destination.storage.addToNode(destination.node, getStack().entity, getStack().amount);
        storage.removeFromNode(node, transfer);
        return transfer;
    }

    public int moveToStorage(StorageSlotView destination) {
        int transfer;
        if (destination.getStack().amount > 1) {
            destination = destination.splitOne();
            if (destination == null) {
                return 0;
            }
        }

        StorageComponent storageComponent = destination.getEntity().getComponent(StorageComponent.class);
        transfer = storageComponent.storage.addEntityAmount(getEntity(), getStack().amount);
        storage.removeFromNode(node, transfer);
        return transfer;
    }

    public int moveTo(StorageSlotView destination, InventoryMove action) {
        // Only insert into empty slots
        if (action != InventoryMove.cancel && !destination.hasStack()) {
            action = InventoryMove.insert;
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
                return moveToStorage(destination);
            case move:
                return moveToSlot(destination);
        }
        return 0;
    }

    public boolean matchesEntity(StorageSlotView other) {
        return other.hasStack() && other.getEntity().compare(getEntity());
    }

    public boolean hasStorageEntity() {
        return hasStack() && getEntity().hasComponent(StorageComponent.class);
    }

    public Entity getEntity() {
        return node.value.entity;
    }

    public DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> getNode() {
        return node;
    }

    public StorageSlotView splitOne() {
        EntityStorage.EntityStack stack = getStack();

        if (stack.amount > 1) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> result =
                    storage.addToNewNode(stack.entity.copy(), 1);
            if (result != null) {
                storage.removeFromNode(node, 1);
                return new StorageSlotView(storage, result);
            }
        } else if (stack.amount == 1) {
            return this;
        }

        return null;
    }

    public boolean exists() {
        return storage.stacks.containsNode(node);
    }

    @Override
    public boolean equals(Object o) {
        return o != null &&
                o.getClass() == getClass() &&
                storage == ((StorageSlotView) o).storage &&
                node == ((StorageSlotView) o).node;
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
