package sonnicon.jade.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.util.Utils;

public interface IPositionMoving extends IPosition {

    void moveTo(float x, float y);

    default void moveTo(IPosition other) {
        if (other == null) {
            moveTo(Float.NaN, Float.NaN);
        } else {
            moveTo(other.getX(), other.getY());
        }
    }

    default void moveTo(Vector3 other) {
        moveTo(other.x, other.y);
    }

    default void moveTo(Vector2 other) {
        moveTo(other.x, other.y);
    }

    default void moveBy(float x, float y) {
        moveTo(getX() + x, getY() + y);
    }


    default boolean rotateBy(float degrees) {
        return rotateTo(Utils.normalizeAngle(this.getRotation() + degrees));
    }

    boolean rotateTo(float degrees);

    default boolean rotateTo(IPosition other) {
        return rotateTo(other.getRotation());
    }
}
