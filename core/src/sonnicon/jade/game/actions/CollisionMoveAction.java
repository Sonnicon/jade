package sonnicon.jade.game.actions;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.collision.Collider;
import sonnicon.jade.game.collision.ColliderMoveSchedule;
import sonnicon.jade.game.collision.Collisions;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class CollisionMoveAction extends Actions.Action implements ColliderMoveSchedule {
    protected float sourceX, sourceY;
    protected float destX, destY;
    protected Entity entity;


    public CollisionMoveAction set(Entity entity, float destX, float destY) {
        this.entity = entity;
        this.destX = destX;
        this.destY = destY;

        assert entity.hasComponent(CollisionComponent.class);
        setDuration(1f);
        return this;
    }


    @Override
    public void onStart() {
        sourceX = entity.getX();
        sourceY = entity.getY();

        Collisions.move(this);
        Actions.interruption = Clock.getTickNum();
    }

    @Override
    public void onFinish() {
        entity.moveTo(getCollider(Clock.getTickNum()));
        Collisions.remove(this);
    }

    @Override
    protected void onInterrupt() {
        entity.moveTo(getX(Clock.getTickNum()), getY(Clock.getTickNum()));
        entity.rotateTo(getRotation(Clock.getTickNum()));
        Collisions.remove(this);
    }

    @Override
    protected void onFrame() {
        entity.rotateTo(getRotation(Clock.getTickNum()));
        entity.moveTo(getX(Clock.getTickNum()), getY(Clock.getTickNum()));
    }

    @Override
    protected void onAlign() {
        entity.moveTo(getX(Clock.getTickNum()), getY(Clock.getTickNum()));
        entity.rotateTo(getRotation(Clock.getTickNum()));
    }

    @Override
    protected void onTick() {
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "sourceX", sourceX,
                "sourceY", sourceY,
                "destX", destX,
                "destY", destY,
                "entity", entity);
    }

    @Override
    public float getX(float tickNum) {
        return MathUtils.lerp(sourceX, destX, getProgress(tickNum));
    }

    @Override
    public float getY(float tickNum) {
        return MathUtils.lerp(sourceY, destY, getProgress(tickNum));
    }

    @Override
    public float getRotation(float tickNum) {
        return entity.getRotation();
    }

    @Override
    public Collider getCollider(float tickNum) {
        CollisionComponent collisionComponent = entity.getComponent(CollisionComponent.class);
        return collisionComponent.collider;
    }

    @Override
    public boolean isMoving() {
        return !interrupted && timeStart + duration > Clock.getTickNum();
    }
}