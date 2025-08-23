package sonnicon.jade.game.collision;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.util.Utils;

public interface IBoundRectangle extends IBound {

    float getWidth();

    float getHeight();

    default boolean containsPoint(IPosition pos, float otherX, float otherY) {
        Vector2 otherVec = new Vector2(otherX, otherY);
        otherVec.rotateDeg(-pos.getRotation());

        float halfWidth = getWidth() / 2f;
        float halfHeight = getHeight() / 2f;
        float x = pos.getX();
        float y = pos.getY();

        float cx = MathUtils.clamp(otherVec.x, x - halfWidth, x + halfWidth);
        float cy = MathUtils.clamp(otherVec.y, y - halfHeight, y + halfHeight);

        return otherVec.sub(cx, cy).len2() <= 0.0001f;
    }

    default boolean intersects(IPosition pos, IBoundRectangle otherBound, IPosition otherPos) {
        return Utils.isIntersecting(this, pos, otherBound, otherPos);
    }

    default boolean intersects(IPosition pos, IBoundCircle otherBound, IPosition otherPos) {
        //todo allocations
        Vector2 otherVec = otherPos.getPosition(new Vector2());
        otherVec.rotateDeg(-pos.getRotation());

        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;
        float x = pos.getX();
        float y = pos.getY();

        float cx = MathUtils.clamp(otherVec.x, x - halfWidth, x + halfWidth);
        float cy = MathUtils.clamp(otherVec.y, y - halfHeight, y + halfHeight);
        otherVec.sub(cx, cy);

        return otherBound.getRadius() * otherBound.getRadius() >= otherVec.len2();
    }
}
