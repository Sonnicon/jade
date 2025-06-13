package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.ICopyable;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.HashSet;
import java.util.Map;

public abstract class Component implements ICopyable, IComparable, IDebuggable {
    public Entity entity;

    public boolean canAddToEntity(Entity entity) {
        HashSet<Class<? extends Component>> deps = getDependencies();
        if (deps != null) {
            for (Class<? extends Component> dep : deps) {
                if (!entity.hasComponentFuzzy(dep)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canRemoveFromEntity(Entity entity) {
        for (Component other : entity.components.values()) {
            if (other != this && other.getDependencies().contains(entity.getClass())) {
                return false;
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

    @Override
    public Component copy() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean compare(IComparable other) {
        return other != null && getClass() == other.getClass();
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("entity", entity);
    }
}
