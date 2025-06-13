package sonnicon.jade.game.collision;

import sonnicon.jade.game.IPosition;

import static com.badlogic.gdx.math.MathUtils.clamp;

public interface IBoundSquare extends IBound {

    float getRadius();

    default boolean containsPoint(IPosition pos, float otherX, float otherY) {
        return Math.abs(pos.getX() - otherX) <= getRadius() && Math.abs(pos.getY() - otherY) < getRadius();
    }

    default boolean intersects(IPosition pos, IBoundSquare otherBound, IPosition otherPos) {
        return Math.abs(pos.getX() - otherPos.getX()) < getRadius() + otherBound.getRadius() &&
                Math.abs(pos.getY() - otherPos.getY()) < getRadius() + otherBound.getRadius();
    }

    default boolean intersects(IPosition pos, IBoundCircle otherBound, IPosition otherPos) {
        float distanceX = otherPos.getX() - clamp(otherPos.getX(), pos.getX() - getRadius(), pos.getX() + getRadius());
        float distanceY = otherPos.getY() - clamp(otherPos.getY(), pos.getY() - getRadius(), pos.getY() + getRadius());
        return (distanceX * distanceX) + (distanceY * distanceY) < (otherBound.getRadius() * otherBound.getRadius());
    }
}
