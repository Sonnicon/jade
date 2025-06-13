package sonnicon.jade.entity.components.world;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.world.World;

public class PositionRelativeComponent extends Component implements IPositionMoving {
    protected float x, y, rotation;

    protected Entity bound;
    // Kept inactive while component is not assigned to an entity
    protected EventTypes.EntityMoveEvent onMoveEvent = ignored -> onBoundMoved();

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        if (bound != null) {
            bound.events.register(onMoveEvent);
            onBoundMoved();
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        if (bound != null) {
            bound.events.unregister(onMoveEvent);
        }
    }

    public PositionRelativeComponent bindToEntity(Entity newBound) {
        // Unregister from previous
        if (bound != null) {
            bound.events.unregister(onMoveEvent);
        }

        // Register to new
        this.bound = newBound;
        if (entity != null) {
            bound.events.register(onMoveEvent);
            onBoundMoved();
        }
        return this;
    }

    protected void onBoundMoved() {
        if (bound == null || entity == null) return;

        float getX = getX();
        float getY = getY();
        float boundRotation = bound.getRotation();

        float newX = bound.getX() + getX * MathUtils.cosDeg(boundRotation) + getY * MathUtils.sinDeg(boundRotation);
        float newY = bound.getY() + -getX * MathUtils.sinDeg(boundRotation) + getY * MathUtils.cosDeg(boundRotation);
        float newRotation = boundRotation + getRotation();

        entity.forceMoveTo(newX, newY);
        entity.rotateTo(newRotation);
    }

    @Override
    public void forceMoveTo(float x, float y) {
        this.x = x;
        this.y = y;
        onBoundMoved();
    }

    @Override
    public boolean rotateTo(float degrees) {
        this.rotation = degrees;
        onBoundMoved();
        return true;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public World getWorld() {
        // Dont follow worlds for now
        return entity.getWorld();
    }
}
