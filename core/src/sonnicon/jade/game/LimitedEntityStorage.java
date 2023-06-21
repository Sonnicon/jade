package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;
import sonnicon.jade.entity.components.StorageComponent;

import java.util.ArrayList;

public class LimitedEntityStorage extends EntityStorage {
    public ArrayList<LimitedEntityStorageSlot> slots = new ArrayList<>();

    public LimitedEntityStorage() {
        super();
    }

    public LimitedEntityStorage addSlot(LimitedEntityStorageSlot slot) {
        slots.add(slot);
        stacks.add(null);
        return this;
    }

    public LimitedEntityStorage addSlot(EntitySize minimumSize, EntitySize maximumSize,
                                        int maximumAmount, SlotType type) {
        return addSlot(new LimitedEntityStorageSlot(minimumSize, maximumSize, maximumAmount, type));
    }

    @Override
    public int addEntityAmount(Entity entity, int amount) {
        EntitySizeComponent sizeComponent = entity.getComponent(EntitySizeComponent.class);
        if (sizeComponent == null || sizeComponent.size == EntitySize.colossal) {
            return 0;
        }
        EntitySize size = sizeComponent.size;
        if (size.value > maximumSize.value || size.value < minimumSize.value) {
            return 0;
        }

        int maxAmount = (capacity - capacityUsed) / size.value;
        if (maxAmount <= 0) return 0;
        amount = Math.min(amount, maxAmount);
        int amountRemaining = amount;

        for (int index = 0; index < slots.size() && amountRemaining > 0; index++) {
            LimitedEntityStorageSlot slot = slots.get(index);
            int transferAmount = 0;

            // If new entity fits
            if (size.value > slot.maximumSize.value || size.value < slot.minimumSize.value) {
                continue;
            }

            EntityStack destination = stacks.get(index);
            if (destination == null) {
                // Stack is empty
                transferAmount = Math.min(amountRemaining, slot.maximumAmount);
                stacks.set(index, new EntityStack(entity, transferAmount));
                return amountRemaining;
            } else if (entity.compare(destination.entity)) {
                // Stack is same type
                transferAmount = Math.min(amountRemaining, slot.maximumAmount - destination.amount);
                destination.amount += transferAmount;
            } else if (entity.hasComponent(StorageComponent.class)) {
                // Stack can store things
                StorageComponent comp = entity.getComponent(StorageComponent.class);
                transferAmount = comp.storage.addEntityAmount(entity, amountRemaining);
            }

            amountRemaining -= transferAmount;
            if (amountRemaining == 0) {
                break;
            }
        }

        capacityUsed += (amount - amountRemaining) * size.value;
        return amount - amountRemaining;
    }

    @Override
    public int removeEntity(Entity entity, int amount) {
        int removed = 0;
        for (int i = 0; amount > 0 && i < stacks.size(); ) {
            EntityStack stack = stacks.get(i);

            if (stack == null || !stack.entity.compare(entity)) {
                i++;
                continue;
            }

            int diff = amount;
            if (stack.amount >= amount) {
                stack.amount -= amount;
                amount = 0;
            } else {
                diff = stack.amount;
                stack.amount = 0;
                amount -= diff;
            }
            if (stack.amount == 0) {
                stacks.set(i, null);
            } else {
                i++;
            }

            removed += diff;
        }

        EntitySizeComponent sizeComponent = entity.getComponent(EntitySizeComponent.class);
        if (sizeComponent != null) {
            capacityUsed -= sizeComponent.size.value * removed;
        }
        return removed;
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
        for (LimitedEntityStorageSlot slot : slots) {
            copy.slots.add(
                    new LimitedEntityStorageSlot(slot.minimumSize, slot.maximumSize, slot.maximumAmount, slot.type));
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

    public static class LimitedEntityStorageSlot {
        public EntitySize minimumSize;
        public EntitySize maximumSize;
        public int maximumAmount;
        public SlotType type;

        public LimitedEntityStorageSlot(EntitySize minimumSize, EntitySize maximumSize) {
            this(minimumSize, maximumSize, Integer.MAX_VALUE, SlotType.generic);
        }

        public LimitedEntityStorageSlot(EntitySize minimumSize, EntitySize maximumSize,
                                        int maximumAmount, SlotType type) {
            this.minimumSize = minimumSize;
            this.maximumSize = maximumSize;
            this.maximumAmount = maximumAmount;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof LimitedEntityStorageSlot) &&
                    minimumSize == ((LimitedEntityStorageSlot) o).minimumSize &&
                    maximumSize == ((LimitedEntityStorageSlot) o).maximumSize &&
                    maximumAmount == ((LimitedEntityStorageSlot) o).maximumAmount &&
                    type == ((LimitedEntityStorageSlot) o).type;
        }
    }

    public enum SlotType {
        hand,
        generic
    }
}