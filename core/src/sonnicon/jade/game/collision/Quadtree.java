package sonnicon.jade.game.collision;

import sonnicon.jade.game.IPosition;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.World;

import java.util.ArrayList;
import java.util.Map;

//todo don't do oop nesting here, just a flat array
//todo check if it works
public class Quadtree implements IPosition, IBoundSquare, IHitbox, IDebuggable {
    private final float x;
    private final float y;
    private final float radius;
    private final World world;
    public final Quadtree parent;

    public ArrayList<Collider> elements = new ArrayList<>();
    public Quadtree[] children = new Quadtree[4];

    protected static final int QUADTREE_LAYER_SIZE = 6;
    private static final byte[] SUBTREE_DIRECTIONS = {
            Directions.NORTHEAST, Directions.SOUTHEAST, Directions.SOUTHWEST, Directions.NORTHWEST
    };

    public Quadtree(float x, float y, float radius, World world) {
        this(x, y, radius, world, null);
    }

    public Quadtree(float x, float y, float radius, World world, Quadtree parent) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.world = world;
        this.parent = parent;
    }

    public Quadtree add(Collider collider) {
        // Not in this quadtree
        if (!collider.intersects(this)) {
            return null;
        }

        // Small enough for us to just add
        if (QUADTREE_LAYER_SIZE > elements.size()) {
            elements.add(collider);
            return this;
        }

        // Create subtrees
        if (children[0] == null) {
            for (int i = 0; i < 4; i++) {
                children[i] = new Quadtree(
                        x + Directions.directionX(SUBTREE_DIRECTIONS[i]) * (radius / 2f),
                        y + Directions.directionY(SUBTREE_DIRECTIONS[i]) * (radius / 2f),
                        radius / 2f, world, this);
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
                    subIndex = -1;
                }
            }
        }

        if (subIndex == -1) {
            // Multiple subtrees: add here
            elements.add(collider);
            return this;
        } else {
            // One subtree
            return children[subIndex].add(collider);
        }
    }

    public Quadtree containing(Collider collider) {
        // It's not here or in children
        if (!collider.intersects(this)) {
            return null;
        }

        // It is in this quadreee
        if (elements.contains(collider)) {
            return this;
        }

        // Must be in child
        if (children[0] == null) {
            for (int i = 0; i < 4; i++) {
                if (collider.intersects(children[i])) {
                    return children[i].containing(collider);
                }
            }
        }

        throw new RuntimeException();
    }

    public void remove(Collider collider) {
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
        ArrayList<Collider> colliders = new ArrayList<>(elements);
        elements.clear();
        colliders.forEach(this::add);
    }

    public boolean anyElementsIntersect(Collider collider) {
        if (anyElementsIntersectHere(collider)) {
            return true;
        }
        if (parent != null && anyElementsIntersectUp(collider)) {
            return true;
        }
        if (children[0] != null && anyElementsIntersectDown(collider)) {
            return true;
        }
        return false;
    }

    private boolean anyElementsIntersectUp(Collider collider) {
        if (anyElementsIntersectHere(collider)) {
            return true;
        }
        if (parent != null && anyElementsIntersectUp(collider)) {
            return true;
        }
        return false;


    }

    private boolean anyElementsIntersectDown(Collider collider) {
        if (anyElementsIntersectHere(collider)) {
            return true;
        }
        if (children[0] == null) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (children[i].anyElementsIntersectDown(collider)) {
                return true;
            }
        }
        return false;
    }

    private boolean anyElementsIntersectHere(Collider collider) {
        for (Collider element : elements) {
            if (element == collider) continue;
            if (element.intersects(collider)) {
                return true;
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
    public float getRadius() {
        return radius;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "x", x,
                "y", y,
                "radius", radius,
                "world", world,
                "elements", elements,
                "children", children
        );
    }
}
