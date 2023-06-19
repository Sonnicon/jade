package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;
import sonnicon.jade.entity.components.StorageComponent;

import java.util.ArrayList;

public class EntityStorage {
    public ArrayList<EntityStack> stacks = new ArrayList<>();
    public int capacityUsed = 0;

    public EntitySize minimumSize = EntitySize.tiny;
    public EntitySize maximumSize = EntitySize.huge;
    public int capacity = Integer.MAX_VALUE;

    public boolean addEntity(Entity entity) {
        return addEntityAmount(entity, 1) == 1;
    }

    public int addEntityAmount(Entity entity, int amount) {
        EntitySizeComponent sizeComponent = entity.getComponent(EntitySizeComponent.class);
        if (sizeComponent == null) {
            return 0;
        }
        EntitySize size = sizeComponent.size;
        if (size == EntitySize.colossal || size.value > maximumSize.value || size.value < minimumSize.value) {
            return 0;
        }

        int maxAmount = (capacity - capacityUsed) / size.value;
        if (maxAmount <= 0) return 0;
        amount = Math.min(amount, maxAmount);

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

    public boolean removeEntity(Entity entity) {
        return removeEntity(entity, 1) == 1;
    }

    public int removeEntity(Entity entity, int amount) {
        int removed = 0;
        for (int i = 0; amount > 0 && i < stacks.size(); ) {
            EntityStack stack = stacks.get(i);
            if (!stack.entity.compare(entity)) {
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
                stacks.remove(i);
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

    public boolean hasMatchingEntity(Entity entity) {
        return stacks.stream().anyMatch(other -> other.entity.compare(entity));
    }

    public boolean containsExactEntity(Entity entity) {
        for (EntityStack stack : stacks) {
            if (stack.entity == entity) {
                return true;
            }
            StorageComponent comp = stack.entity.getComponent(StorageComponent.class);
            if (comp != null && comp.storage.containsExactEntity(entity)) {
                return true;
            }
        }
        return false;
    }

    public EntityStorage copy() {
        EntityStorage copy = new EntityStorage();
        copy.minimumSize = minimumSize;
        copy.maximumSize = maximumSize;
        copy.capacity = capacity;
        copy.capacityUsed = capacityUsed;

        for (EntityStack stack : stacks) {
            copy.stacks.add(stack.copy());
        }
        return copy;
    }

    public boolean compare(EntityStorage other) {
        if (stacks == null || other == null || other.stacks == null ||
                capacity != other.capacity || minimumSize != other.minimumSize || maximumSize != other.maximumSize ||
                stacks.size() != other.stacks.size()) {
            return false;
        }
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).compare(other.stacks.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static class EntityStack {
        public Entity entity;
        public int amount;

        public EntityStack() {

        }

        public EntityStack(Entity entity) {
            this(entity, 1);
        }

        public EntityStack(Entity entity, int amount) {
            this.entity = entity;
            this.amount = amount;
        }

        public EntityStack copy() {
            return new EntityStack(entity, amount);
        }

        public boolean compare(EntityStack other) {
            return amount == other.amount && entity.compare(other.entity);
        }
    }

}
