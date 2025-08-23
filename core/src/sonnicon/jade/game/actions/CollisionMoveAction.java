package sonnicon.jade.game.actions;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.collision.Collider;
import sonnicon.jade.game.collision.ColliderMoveSchedule;
import sonnicon.jade.game.collision.Collisions;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class CollisionMoveAction extends Actions.Action implements ColliderMoveSchedule {
    protected float diffX, diffY, diffRotation;
    protected Entity entity;

    protected float totalX, totalY, totalRotation;


    public CollisionMoveAction set(Entity entity, float diffX, float diffY, float diffRotation) {
        this.entity = entity;
        this.diffX = diffX;
        this.diffY = diffY;
        this.diffRotation = diffRotation;


        assert entity.hasComponent(CollisionComponent.class);
        setDuration(1f);
        return this;
    }

    @Override
    public void onStart() {
        if (Math.abs(diffX) < 0.001f && Math.abs(diffY) < 0.001f) {
            interrupt();
        }

        Collisions.move(this);
        Actions.interruption = Clock.getTickNum();
        totalX = 0f;
        totalY = 0f;
        totalRotation = 0f;
    }

    @Override
    public void onFinish() {
        entity.moveBy(getDeltaX(Clock.getTickNum()) - totalX, getDeltaY(Clock.getTickNum()) - totalY);
        entity.rotateBy(getDeltaRotation(Clock.getTickNum()) - totalRotation);
        Collisions.remove(this);
    }

    @Override
    protected void onInterrupt() {
        onFinish();
    }

    @Override
    protected void onFrame() {
        float dx = getDeltaX(Clock.getTickNum());
        float dy = getDeltaY(Clock.getTickNum());
        float dr = getDeltaRotation(Clock.getTickNum());

        entity.moveBy(dx - totalX, dy - totalY);
        entity.rotateBy(dr - totalRotation);

        totalX = dx;
        totalY = dy;
        totalRotation = dr;
    }

    @Override
    protected void onAlign() {
        onFrame();
    }

    @Override
    protected void onTick() {
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "diffX", diffX,
                "diffY", diffY,
                "diffRotation", diffRotation,
                "entity", entity);
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public float getDeltaX(float tickNum) {
        return diffX * getProgress(tickNum);
    }

    @Override
    public float getDeltaY(float tickNum) {
        return diffY * getProgress(tickNum);
    }

    @Override
    public float getDeltaRotation(float tickNum) {
        return diffRotation * getProgress(tickNum);
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