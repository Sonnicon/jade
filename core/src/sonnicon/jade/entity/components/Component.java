package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.ICopyable;

import java.util.HashSet;

public abstract class Component implements ICopyable, IComparable {
    protected Entity entity;

    public boolean canAddToEntity(Entity entity) {
        HashSet<Class<? extends Component>> deps = getDependencies();
        if (deps != null) {
            for (Class<? extends Component> dep : deps) {
                if (!entity.components.containsKey(dep)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addToEntity(Entity entity) {
        this.entity = entity;
    }

    public void removeFromEntity(Entity entity) {
        this.entity = null;
    }

    public HashSet<Class<? extends Component>> getDependencies() {
        return null;
    }

    public Class<? extends Component> getKeyClass() {
        return getClass();
    }

    public void dispose() {

    }

    @Override
    public Component copy() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new AssertionError();
        }
    }
}
