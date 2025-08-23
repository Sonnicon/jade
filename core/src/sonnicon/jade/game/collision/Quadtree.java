package sonnicon.jade.game.collision;

import sonnicon.jade.game.Clock;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.World;

import java.util.ArrayList;
import java.util.Map;

//todo don't do oop nesting here, just a flat array
public class Quadtree implements IPosition, IBoundSquare, IHitbox, IDebuggable {
    private final float x;
    private final float y;
    private final float diameter;
    private final World world;

    public ArrayList<Collider> elements = new ArrayList<>();
    public Quadtree[] children = new Quadtree[4];

    protected static final int QUADTREE_LAYER_SIZE = 6;
    private static final byte[] SUBTREE_DIRECTIONS = {
            Directions.NORTHEAST, Directions.SOUTHEAST, Directions.SOUTHWEST, Directions.NORTHWEST
    };

    public Quadtree(float x, float y, float diameter, World world) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.world = world;
    }

    public Quadtree add(Collider collider) {
        assert Clock.getPhase() == Clock.ClockPhase.tick;
        // Not in this quadtree
        if (!collider.intersects(this)) {
            return null;
        }

        // Small enough for us to just add
        if (elements.size() < QUADTREE_LAYER_SIZE) {
            elements.add(collider);
            return this;
        }

        // Create subtrees
        if (children[0] == null) {
            for (int i = 0; i < 4; i++) {
                children[i] = new Quadtree(
                        x + Directions.directionX(SUBTREE_DIRECTIONS[i]) * (diameter / 4f),
                        y + Directions.directionY(SUBTREE_DIRECTIONS[i]) * (diameter / 4f),
                        diameter / 2f, world);
            }
            rebuildDeepen();
        }

        // Which subtrees are we inside
        int subIndex = -1;
        for (int i = 0; i < 4; i++) {
            if (collider.intersects(children[i])) {
                if (subIndex == -1) {
                    subIndex = i;
                } else {
                    elements.add(collider);
                    return this;
                }
            }
        }

        assert subIndex != -1;
        return children[subIndex].add(collider);
    }

    public void remove(Collider collider) {
        assert Clock.getPhase() == Clock.ClockPhase.tick;
        //todo shrink layers
        if (elements.contains(collider)) {
            elements.remove(collider);
        } else if (children[0] != null) {
            for (int i = 0; i < 4; i++) {
                if (collider.intersects(children[i])) {
                    children[i].remove(collider);
                    break;
                }
            }
        }
    }

    protected void rebuildDeepen() {
        assert Clock.getPhase() == Clock.ClockPhase.tick;
        ArrayList<Collider> colliders = new ArrayList<>(elements);
        elements.clear();
        colliders.forEach(this::add);
    }

    public boolean anyElementsIntersect(Collider collider) {
        assert Clock.getPhase() == Clock.ClockPhase.tick;

        // This quadtree is not even involved
        if (!collider.intersects(this)) {
            return false;
        }

        // Intersects something at this level
        for (Collider element : elements) {
            if (element == collider) continue;
            if (element.intersects(collider)) {
                return true;
            }
        }

        // Intersects children
        if (children[0] != null) {
            for (int i = 0; i < 4; i++) {
                if (children[i].anyElementsIntersect(collider)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public float getDiameter() {
        return diameter;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "x", x,
                "y", y,
                "diameter", diameter,
                "world", world,
                "elements", elements,
                "children", children
        );
    }
}
