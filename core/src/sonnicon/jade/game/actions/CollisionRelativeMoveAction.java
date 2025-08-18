package sonnicon.jade.game.actions;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.collision.ColliderMoveSchedule;

public class CollisionRelativeMoveAction extends CollisionMoveAction implements ColliderMoveSchedule {

    public CollisionRelativeMoveAction set(Entity entity, float deltaX, float deltaY) {
        super.set(entity, deltaX, deltaY);
        return this;
    }

    @Override
    public float getX(float tickNum) {
        return sourceX + MathUtils.lerp(0f, destX, getProgress(tickNum));
    }

    @Override
    public float getY(float tickNum) {
        return sourceY + MathUtils.lerp(0f, destY, getProgress(tickNum));
    }
}