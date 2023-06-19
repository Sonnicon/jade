package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;

import java.util.ArrayList;

public class LimitedEntityStorage extends EntityStorage {
    protected ArrayList<LimitedEntityStorageSlot> slots;

    public LimitedEntityStorage() {
        super();
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
                amountRemaining -= transferAmount;
                return amountRemaining;
            } else if (entity.compare(destination.entity)) {
                // Stack is same type
                transferAmount = Math.min(amountRemaining, slot.maximumAmount - destination.amount);
                destination.amount += transferAmount;
                amountRemaining -= transferAmount;
            } else {
                //todo
            }

            if (amountRemaining == 0) {
                return amount;
            }

        }


        //todo
        EntityStack destination = null;
        for (EntityStack stack : stacks) {
            if (stack.entity.compare(entity)) {
                destination = stack;
                break;
            }
        }
        if (destination != null) {
            destination.amount += amount;
        } else {
            stacks.add(new EntityStack(entity, amount));
        }

        capacityUsed += amount * size.value;
        return amount;
    }

    @Override
    public boolean removeEntity(Entity entity) {
        return super.removeEntity(entity);
    }

    @Override
    public int removeEntity(Entity entity, int amount) {
        return super.removeEntity(entity, amount);
    }

    @Override
    public boolean hasMatchingEntity(Entity entity) {
        return super.hasMatchingEntity(entity);
    }

    @Override
    public boolean containsExactEntity(Entity entity) {
        return super.containsExactEntity(entity);
    }

    @Override
    public EntityStorage copy() {
        return super.copy();
    }

    @Override
    public boolean compare(EntityStorage other) {
        return super.compare(other);
    }

    protected class LimitedEntityStorageSlot {
        public EntitySize minimumSize;
        public EntitySize maximumSize;
        public int maximumAmount = Integer.MAX_VALUE;

        public LimitedEntityStorageSlot(EntitySize min, EntitySize max) {
            this.minimumSize = min;
            this.maximumSize = max;
        }
    }
}