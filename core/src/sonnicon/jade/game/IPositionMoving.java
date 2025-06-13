package sonnicon.jade.game;

public interface IPositionMoving extends IPosition {

    void forceMoveTo(float x, float y);

    default void forceMoveTo(IPosition other) {
        forceMoveTo(other.getX(), other.getY());
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
