package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.util.DoubleLinkedList;
import sonnicon.jade.util.ICopyable;

import java.util.Iterator;

public class EntityStorage implements ICopyable {
    public final DoubleLinkedList<EntityStorageSlot> slots = new DoubleLinkedList<>();
    private int capacityUsed = 0;

    public EntitySize minimumSize = EntitySize.tiny;
    public EntitySize maximumSize = EntitySize.huge;
    public int capacity = Integer.MAX_VALUE;

    public boolean addEntity(Entity entity) {
        return addEntityAmount(entity, 1) == 1;
    }

    public int addEntityAmount(Entity entity, int amount) {
        amount = getMaxAddAmount(entity, amount);

        // Add entities to existing slots
        Iterator<EntityStorageSlot> iter = slots.iterator();
        int remaining = amount;
        while (iter.hasNext() && remaining > 0) {
            EntityStorageSlot slot = iter.next();
            remaining -= slot.add(entity, remaining);
        }

        // Leftover entities go in a new slot
        if (remaining > 0) {
            EntityStorageSlot newSlot = addToNewSlot(entity, remaining);
            if (newSlot != null) {
                remaining -= newSlot.getAmount();
            }
        }

        // Update capacityUsed
        updateCapacityUsed(entity, amount - remaining);
        return amount - remaining;
    }

    public int removeEntityAmount(Entity entity, int amount) {
        int remaining = amount;
        Iterator<EntityStorageSlot> iter = slots.iterator();
        while (iter.hasNext() && remaining > 0) {
            EntityStorageSlot slot = iter.next();
            if (!slot.isEmpty() && slot.getEntity().compare(entity)) {
                remaining -= slot.remove(remaining);
            }
        }

        return amount - remaining;
    }


    public EntityStorageSlot addToNewSlot(Entity entity, int amount) {
        return addToNewSlot(entity, amount, false);
    }

    protected EntityStorageSlot addToNewSlot(Entity entity, int amount, boolean force) {
        if (!force) {
            if ((amount = getMaxAddAmount(entity, amount)) <= 0) {
                return null;
            }
        }

        return appendNewSlot(entity, amount);
    }

    // we don't use the slot event system for these because that would defeat the point of lazy event handler
    protected void onSlotChanged(EntityStorageSlot slot, Entity entity, int oldAmount, int newAmount) {
        if (newAmount == 0) {
            disconnectSlot(slot);
        } else {
            updateCapacityUsed(entity, newAmount - oldAmount);
        }
    }

    protected void onSlotDisconnected(EntityStorageSlot slot) {
        if (!slot.isEmpty()) {
            updateCapacityUsed(slot.getEntity(), -slot.getAmount());
        }
    }

    public void updateCapacityUsed(Entity entity, int amount) {
        EntitySizeComponent sizeComponent = entity.getComponent(EntitySizeComponent.class);
        if (sizeComponent != null) {
            capacityUsed += sizeComponent.size.value * amount;
        }
    }

    public void updateCapacityUsed() {
        capacityUsed = 0;
        for (EntityStorageSlot slot : slots) {
            if (!slot.isEmpty()) {
                updateCapacityUsed(slot.getEntity(), slot.getAmount());
            }
        }
    }

    public boolean hasMatchingEntity(Entity entity) {
        return slots.stream().anyMatch(other -> other.getEntity().compare(entity));
    }

    public boolean containsExactEntity(Entity entity) {
        for (EntityStorageSlot slot : slots) {
            if (slot.getEntity() == entity) {
                return true;
            }
            StorageComponent comp = slot.getEntity().getComponent(StorageComponent.class);
            if (comp != null && comp.storage.containsExactEntity(entity)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxAddAmount(Entity entity, int amount) {
        if (!EntitySizeComponent.fitsSize(entity, minimumSize, maximumSize)) {
            return 0;
        }
        return Math.max(0,
                Math.min(amount,
                        (capacity - capacityUsed) / entity.getComponent(EntitySizeComponent.class).size.value));
    }

    protected EntityStorageSlot appendNewSlot(Entity entity, int amount) {
        DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node = new DoubleLinkedList.DoubleLinkedListNode<>();
        node.value = new EntityStorageSlot(entity, amount, this, node);
        slots.addNode(node);
        return node.value;
    }

    public boolean appendSlot(EntityStorageSlot slot) {
        DoubleLinkedList.DoubleLinkedListNode<EntityStorageSlot> node = new DoubleLinkedList.DoubleLinkedListNode<>();
        node.value = slot;
        slots.addNode(node);
        slot.attach(this, node);
        return true;
    }

    public boolean disconnectSlot(EntityStorageSlot slot) {
        slot.disconnect();
        return true;
    }

    @Override
    public EntityStorage copy() {
        EntityStorage copy = (EntityStorage) ICopyable.super.copy();
        copy.minimumSize = minimumSize;
        copy.maximumSize = maximumSize;
        copy.capacity = capacity;
        copy.capacityUsed = capacityUsed;

        for (EntityStorageSlot slot : slots) {
            copy.appendSlot(slot.copy());
        }
        return copy;
    }

    public boolean compare(EntityStorage other) {
        if (this == other) {
            return true;
        }

        if (slots == null || other == null || other.slots == null ||
                capacity != other.capacity || minimumSize != other.minimumSize || maximumSize != other.maximumSize ||
                slots.size() != other.slots.size()) {
            return false;
        }
        for (int i = 0; i < slots.size(); i++) {
            if (!slots.get(i).compare(other.slots.get(i))) {
                return false;
            }
        }
        return true;
    }
}
