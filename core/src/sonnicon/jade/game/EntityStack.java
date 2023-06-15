package sonnicon.jade.game;

import sonnicon.jade.entity.Entity;

public class EntityStack {
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
