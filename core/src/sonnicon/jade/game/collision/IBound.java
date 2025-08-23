package sonnicon.jade.game.collision;

import sonnicon.jade.game.Clock;
import sonnicon.jade.game.IPosition;

public interface IBound {

    default boolean containsPoint(IPosition pos, IPosition other) {
        return containsPoint(pos, other.getX(), other.getY());
    }

    boolean containsPoint(IPosition pos, float otherX, float otherY);

    default boolean intersects(IPosition pos, IBound otherBound, IPosition otherPos) {
        assert Clock.getPhase() == Clock.ClockPhase.tick;
        if (otherBound instanceof IBoundRectangle) return intersects(pos, (IBoundRectangle) otherBound, otherPos);
        if (otherBound instanceof IBoundCircle) return intersects(pos, (IBoundCircle) otherBound, otherPos);

        assert (otherBound != null);
        throw new UnsupportedOperationException("Unknown collision type.");
    }

    boolean intersects(IPosition pos, IBoundRectangle otherBound, IPosition otherPos);

    boolean intersects(IPosition pos, IBoundCircle otherBound, IPosition otherPos);
}
