package sonnicon.jade.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public interface IPositionMoving extends IPosition {

    void forceMoveTo(float x, float y);

    default void forceMoveTo(IPosition other) {
        if (other == null) {
            forceMoveTo(Float.NaN, Float.NaN);
        } else {
            forceMoveTo(other.getX(), other.getY());
        }
    }

    default void forceMoveTo(Vector3 other) {
        forceMoveTo(other.x, other.y);
    }

    default void forceMoveTo(Vector2 other) {
        forceMoveTo(other.x, other.y);
    }

    default void forceMoveBy(float x, float y) {
        forceMoveTo(getX() + x, getY() + y);
    }

    default float canMoveBy(float x, float y) {
        return 1f;
    }

    default float moveBy(float x, float y) {
        float scale = canMoveBy(x, y);
        forceMoveBy(x * scale, y * scale);
        return scale;
    }


    default boolean rotateBy(float degrees) {
        //todo rotation utils, incl. fix for (x <= -360)
        return rotateTo(this.getRotation() + degrees);
    }

    boolean rotateTo(float degrees);
}
