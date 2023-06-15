package sonnicon.jade.game;

import java.util.ArrayList;

public class EntityStorage {
    public ArrayList<EntityStack> stacks = new ArrayList<>();
    public EntitySize minimumSize = EntitySize.tiny;
    public EntitySize maximumSize = EntitySize.huge;
    public int capacity = EntitySize.medium.value * 3;

    public EntityStorage copy() {
        EntityStorage copy = new EntityStorage();
        copy.minimumSize = minimumSize;
        copy.maximumSize = maximumSize;
        copy.capacity = capacity;
        
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
}
