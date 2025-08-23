package sonnicon.jade.game.collision;

import sonnicon.jade.game.IPosition;

public interface IBoundCircle extends IBound {

    float getRadius();

    default boolean containsPoint(IPosition pos, float otherX, float otherY) {
        float dx = Math.abs(otherX - pos.getX());
        float dy = Math.abs(otherY - pos.getY());
        return dx * dx + dy * dy < getRadius() * getRadius();
    }

    default boolean intersects(IPosition pos, IBoundRectangle otherBound, IPosition otherPos) {
        return otherBound.intersects(otherPos, this, pos);
    }

    default boolean intersects(IPosition pos, IBoundCircle otherBound, IPosition otherPos) {
        float dx = Math.abs(otherPos.getX() - pos.getX());
        float dy = Math.abs(otherPos.getX() - pos.getY());
        return Math.sqrt(dx * dx + dy * dy) < getRadius() + otherBound.getRadius();
    }
}
