package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.EntitySizeComponent;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.util.DoubleLinkedList;

import java.util.Iterator;

public class EntityStorage {
    public DoubleLinkedList<EntityStack> stacks = new DoubleLinkedList<>();
    protected int capacityUsed = 0;

    public EntitySize minimumSize = EntitySize.tiny;
    public EntitySize maximumSize = EntitySize.huge;
    public int capacity = Integer.MAX_VALUE;

    public boolean addEntity(Entity entity) {
        return addEntityAmount(entity, 1) == 1;
    }

    public int addEntityAmount(Entity entity, int amount) {
        amount = findMaxAmount(entity, amount);

        // Add entities to existing nodes
        Iterator<DoubleLinkedList.DoubleLinkedListNode<EntityStack>> iter = stacks.nodeIterator();
        int remaining = amount;
        while (iter.hasNext() && remaining > 0) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStack> node = iter.next();
            if (node.value == null || node.value.entity.compare(entity)) {
                remaining -= addToNode(node, entity, remaining);
            }
        }

        // Leftover entities go in a new node
        if (remaining > 0) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStack> newNode = addToNewNode(entity, remaining);
            if (newNode != null) {
                remaining -= newNode.value.amount;
            }
        }

        // Update capacityUsed
        updateCapacityUsed(entity, amount - remaining);
        return amount - remaining;
    }

    public boolean removeEntity(Entity entity) {
        return removeEntity(entity, 1) == 1;
    }

    public int removeEntity(Entity entity, int amount) {
        int remaining = amount;
        Iterator<DoubleLinkedList.DoubleLinkedListNode<EntityStack>> iter = stacks.nodeIterator();
        while (iter.hasNext() && remaining > 0) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStack> node = iter.next();
            if (node.value.entity.compare(entity)) {
                remaining -= removeFromNode(node, remaining);
            }
        }

        return amount - remaining;
    }

    public int addToNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, int amount) {
        return addToNode(node, node.value.entity, amount, true);
    }

    protected int addToNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, int amount, boolean check) {
        return addToNode(node, node.value.entity, amount, check);
    }

     public int addToNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, Entity entity, int amount) {
        return addToNode(node, entity, amount, true);
     }

     protected int addToNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, Entity entity, int amount, boolean check) {
        if (check) {
            if ((amount = findMaxAmount(entity, amount)) <= 0) {
                return 0;
            }
        }
        if (node.value == null) {
            node.value = new EntityStack(entity, amount);
        } else {
            node.value.amount += amount;
        }
        updateCapacityUsed(entity, amount);
        return amount;
    }

    public DoubleLinkedList.DoubleLinkedListNode<EntityStack> addToNewNode(Entity entity, int amount) {
        return addToNewNode(entity, amount, true);
    }

    protected DoubleLinkedList.DoubleLinkedListNode<EntityStack> addToNewNode(Entity entity, int amount, boolean check) {
        if (check) {
            if ((amount = findMaxAmount(entity, amount)) <= 0) {
                return null;
            }
        }

        DoubleLinkedList.DoubleLinkedListNode<EntityStack> node =
                new DoubleLinkedList.DoubleLinkedListNode<>(new EntityStack(entity, amount));
        stacks.addNode(node);
        updateCapacityUsed(entity, amount);
        return node;
    }

    public int removeFromNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node, int amount) {
        if (amount == 0) {
            return 0;
        }
        // removing everything
        if (amount >= node.value.amount) {
            amount = node.value.amount;
            removeAllFromNode(node);
            return amount;
        } else {
            node.value.amount -= amount;
        }
        updateCapacityUsed(node.value.entity, -amount);
        return amount;
    }

    public void removeAllFromNode(DoubleLinkedList.DoubleLinkedListNode<EntityStack> node) {
        stacks.removeNode(node);
        if (node.value != null) {
            updateCapacityUsed(node.value.entity, -node.value.amount);
            node.value.amount = 0;
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
        for (EntityStack stack : stacks) {
            if (stack != null) {
                updateCapacityUsed(stack.entity, stack.amount);
            }
        }
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

    protected int findMaxAmount(Entity entity, int amount) {
        if (!EntitySizeComponent.fitsSize(entity, minimumSize, maximumSize)) {
            return 0;
        }
        return Math.max(0,
                Math.min(amount,
                        (capacity - capacityUsed) / entity.getComponent(EntitySizeComponent.class).size.value));
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
            return new EntityStack(entity.copy(), amount);
        }

        public boolean compare(EntityStack other) {
            return amount == other.amount && entity.compare(other.entity);
        }
    }

}
