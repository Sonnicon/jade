package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;
import sonnicon.jade.util.DoubleLinkedList;

import java.util.ArrayList;

public class LimitedEntityStorage extends EntityStorage {
    public ArrayList<StorageSlotRestriction> slots = new ArrayList<>();

    public LimitedEntityStorage() {
        super();
    }

    public LimitedEntityStorage addSlot(StorageSlotRestriction slot) {
        slots.add(slot);
        stacks.add(null);
        return this;
    }

    public LimitedEntityStorage addSlot(EntitySize minimumSize, EntitySize maximumSize,
                                        int maximumAmount, SlotType type) {
        return addSlot(new StorageSlotRestriction(minimumSize, maximumSize, maximumAmount, type));
    }

    @Override
    protected int addToNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, Entity entity, int amount, boolean check) {
        int index = stacks.indexOfNode(node);
        StorageSlotRestriction restriction = slots.get(index);

        int transferred;
        if (check) {
            // Wrong size
            if (!EntitySizeComponent.fitsSize(entity, restriction.minimumSize, restriction.maximumSize)) {
                return 0;
            }
            transferred = Math.min(amount, restriction.maximumAmount - (node.value == null ? 0 : node.value.amount));
        } else {
            transferred = amount;
        }

        if (transferred == 0) {
            return 0;
        }

        if (node.value == null) {
            node.value = new EntityStack(entity, transferred);
        } else {
            node.value.amount += transferred;
        }
        updateCapacityUsed(entity, transferred);
        return transferred;
    }

    @Override
    protected DoubleLinkedList.DoubleLinkedListNode<EntityStack> addToNewNode(Entity entity, int amount, boolean check) {
        return null;
    }

    @Override
    public void removeAllFromNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node) {
        if (node.value != null) {
            node.value = null;
        }
    }

    @Override
    public EntityStorage copy() {
        LimitedEntityStorage copy = new LimitedEntityStorage();
        copy.minimumSize = minimumSize;
        copy.maximumSize = maximumSize;
        copy.capacity = capacity;
        copy.capacityUsed = capacityUsed;

        for (EntityStack stack : stacks) {
            copy.stacks.add(stack.copy());
        }
        for (StorageSlotRestriction slot : slots) {
            copy.slots.add(
                    new StorageSlotRestriction(slot.minimumSize, slot.maximumSize, slot.maximumAmount, slot.type));
        }
        return copy;
    }

    @Override
    public boolean compare(EntityStorage other) {
        if (!(other instanceof LimitedEntityStorage) || ((LimitedEntityStorage) other).slots.size() != slots.size()) {
            return false;
        }

        for (int i = 0; i < slots.size(); i++) {
            if (!slots.get(i).equals(((LimitedEntityStorage) other).slots.get(i))) {
                return false;
            }
        }

        return super.compare(other);
    }

    public static class StorageSlotRestriction {
        public EntitySize minimumSize;
        public EntitySize maximumSize;
        public int maximumAmount;
        public SlotType type;

        public StorageSlotRestriction(EntitySize minimumSize, EntitySize maximumSize) {
            this(minimumSize, maximumSize, Integer.MAX_VALUE, SlotType.generic);
        }

        public StorageSlotRestriction(EntitySize minimumSize, EntitySize maximumSize,
                                      int maximumAmount, SlotType type) {
            this.minimumSize = minimumSize;
            this.maximumSize = maximumSize;
            this.maximumAmount = maximumAmount;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof StorageSlotRestriction) &&
                    minimumSize == ((StorageSlotRestriction) o).minimumSize &&
                    maximumSize == ((StorageSlotRestriction) o).maximumSize &&
                    maximumAmount == ((StorageSlotRestriction) o).maximumAmount &&
                    type == ((StorageSlotRestriction) o).type;
        }
    }

    public enum SlotType {
        hand,
        generic
    }
}