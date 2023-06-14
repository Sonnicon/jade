package sonnicon.jade.game.components;

import sonnicon.jade.game.Entity;

import java.util.HashSet;

public abstract class Component {
    protected Entity entity;

    public boolean canAddToEntity(Entity entity) {
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

    public void dispose() {

    }

    public abstract Component copy();

    public abstract boolean compare(Component other);
}
