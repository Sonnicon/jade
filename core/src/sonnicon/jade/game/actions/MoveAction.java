package sonnicon.jade.game.actions;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.util.DebugTarget;
import sonnicon.jade.util.Utils;

import java.util.Map;

//TODO rewrite this class
public class MoveAction extends Actions.Action {
    protected IPosition source;
    protected IPosition target;
    protected Entity entity;

    public MoveAction set(Entity entity, IPosition target) {
        this.entity = entity;
        this.target = target;
        return this;
    }


    @Override
    public void onStart() {
        //todo get rid of the allocations
        IPositionMoving debugTarget = new DebugTarget();
        debugTarget.forceMoveTo(entity);
        source = debugTarget;

        //todo add simultaneous movement to all colliders, solve
        CollisionComponent collisionComponent = entity.getComponent(CollisionComponent.class);
        if (collisionComponent != null) {
            float dx = target.getX() - source.getX();
            float dy = target.getY() - source.getY();
            float possibleMove = collisionComponent.getMaxMoveDistance(dx, dy);
            if (possibleMove < 1f) {
                interrupt();
            }
        }
    }

    @Override
    public void onFinish() {
    }

    @Override
    protected void onInterrupt() {

    }

    @Override
    protected void onFrame() {
        float progress = getProgress();
        entity.forceMoveTo(
                Utils.lerpX(source, target, progress),
                Utils.lerpY(source, target, progress));
    }

    @Override
    protected void onAlign() {

    }

    @Override
    protected void onTick() {

    }

    //todo remove this when simultaenous collisions
    public static MoveAction createChain(Entity e, IPosition target, float timePerTile) {
        float eX = e.getX();
        float eY = e.getY();
        float dx = target.getX() - eX;
        float dy = target.getY() - eY;
        float length = Utils.pythag(dy, dx);
        float unitX = dx / length;
        float unitY = dy / length;


        MoveAction result = Actions.obtain(MoveAction.class);
        MoveAction actionIter = result;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                MoveAction nextAction = Actions.obtain(MoveAction.class);
                actionIter.then(nextAction);
                actionIter = nextAction;
            }
            DebugTarget locTo = new DebugTarget(eX + unitX * i, eY + unitY * i);
            actionIter.set(e, locTo);
            actionIter.setDuration(timePerTile);
        }

        if (length > Math.floor(length)) {
            MoveAction nextAction = Actions.obtain(MoveAction.class);
            nextAction.set(e, target);
            actionIter.then(nextAction);
            actionIter.setDuration(timePerTile);
        }

        return result;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "source", source,
                "target", target,
                "entity", entity);
    }
}