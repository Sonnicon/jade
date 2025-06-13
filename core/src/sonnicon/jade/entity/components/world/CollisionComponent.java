package sonnicon.jade.entity.components.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.collision.Collider;
import sonnicon.jade.game.collision.Quadtree;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Chunk;

import java.util.ArrayList;
import java.util.Map;

public class CollisionComponent extends Component {
    public Collider collider;
    public ArrayList<Quadtree> quadtrees = new ArrayList<>();

    public static final float COLLISION_STEP_SIZE = 0.25f;

    //todo get rid of all of these temporary arrays all over the place.
    private static final ArrayList<Chunk> TEMP_CHUNKS1 = new ArrayList<>();
    private static final ArrayList<Chunk> TEMP_CHUNKS2 = new ArrayList<>();

    public CollisionComponent(Collider collider) {
        this.collider = collider;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);

        TEMP_CHUNKS1.clear();
        collider.containingChunks(TEMP_CHUNKS1);
        for (Chunk c : TEMP_CHUNKS1) {
            c.collisionTree.add(collider);
            quadtrees.add(c.collisionTree);
        }

        entity.events.register((EventTypes.EntityMoveEvent) CollisionComponent::onEntityMove);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        quadtrees.forEach(q -> q.remove(collider));

        entity.events.unregister((EventTypes.EntityMoveEvent) CollisionComponent::onEntityMove);
    }

    public float getMaxMoveDistance(float dx, float dy) {
        //todo find a better way to do this than moving the collision box
        int maxSteps = (int) Math.ceil(Math.sqrt(dx * dx + dy * dy) / COLLISION_STEP_SIZE);
        float stepX = dx / maxSteps;
        float stepY = dy / maxSteps;

        float originalX = collider.getX();
        float originalY = collider.getY();

        for (int i = 0; i < maxSteps; i++) {
            float newX = originalX + stepX * (i + 1);
            float newY = originalY + stepY * (i + 1);
            // Move collider to new position without updating quadtree
            collider.forceMoveTo(newX, newY);
            //todo
            TEMP_CHUNKS2.clear();
            collider.containingChunks(TEMP_CHUNKS2);
            for (Chunk c : TEMP_CHUNKS2) {
                if (c.collisionTree.anyElementsIntersect(collider)) {
                    collider.forceMoveTo(originalX, originalY);
                    return (float) i / maxSteps;
                }
            }
        }
        collider.forceMoveTo(originalX, originalY);
        return 1f;
    }

    protected static void onEntityMove(Entity e) {
        //todo
        CollisionComponent comp = e.getComponent(CollisionComponent.class);
        comp.quadtrees.forEach(q -> q.remove(comp.collider));
        comp.quadtrees.clear();
        comp.collider.forceMoveTo(e.getX(), e.getY());
        TEMP_CHUNKS1.clear();
        comp.collider.containingChunks(TEMP_CHUNKS1);
        for (Chunk c : TEMP_CHUNKS1) {
            c.collisionTree.add(comp.collider);
            comp.quadtrees.add(c.collisionTree);
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "collider", collider,
                "quadtrees", quadtrees);
    }
}
