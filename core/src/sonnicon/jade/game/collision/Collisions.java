package sonnicon.jade.game.collision;

import sonnicon.jade.game.Clock;
import sonnicon.jade.world.Chunk;

import java.util.ArrayList;

public class Collisions implements Clock.IOnTick {
    protected static ArrayList<ColliderMoveSchedule> schedules = new ArrayList<>();

    public static final float TICK_STEP_SIZE = 0.1f;
    private static final ArrayList<Chunk> TEMP_CHUNKS2 = new ArrayList<>();

    public static Collisions collisions;

    public Collisions() {
        //todo tick schedule priorities, so this goes last
        Clock.register(this);
    }

    public static void move(ColliderMoveSchedule col) {
        assert col.isMoving();
        schedules.add(col);
    }

    @Override
    public void onTick(float delta) {
        schedules.removeIf(a -> !a.isMoving());
        if (schedules.isEmpty()) {
            return;
        }

        //todo rework to split over distance and not time
        float timeToNextTick = Clock.getTimeToNextTick();
        int numSteps = (int) Math.ceil(timeToNextTick / TICK_STEP_SIZE);
        float tickTimeStep = timeToNextTick / numSteps;
        for (int step = 0; step < numSteps; step++) {
            if (schedules.isEmpty()) {
                return;
            }

            //todo put an interpolation on this, so close is more precise (or binarysearch)
            float clockTime = Clock.getTickNum() + tickTimeStep * (step + 1);

            // First, back to base positions
            for (ColliderMoveSchedule schedule : schedules) {
                Collider collider = schedule.getCollider(clockTime);
                collider.moveTo(schedule.getEntity());
                collider.rotateTo(schedule.getEntity());
            }
            // Then, add all deltas
            for (ColliderMoveSchedule schedule : schedules) {
                Collider collider = schedule.getCollider(clockTime);
                collider.moveBy(schedule.getDeltaX(clockTime), schedule.getDeltaY(clockTime));
                collider.rotateBy(schedule.getDeltaRotation(clockTime));
            }

            // Check collision
            boolean collided = false;
            for (ColliderMoveSchedule schedule : schedules) {
                Collider collider = schedule.getCollider(clockTime);
                TEMP_CHUNKS2.clear();
                collider.containingChunks(TEMP_CHUNKS2);
                for (Chunk chunk : TEMP_CHUNKS2) {
                    if (chunk.collisionTree.anyElementsIntersect(collider)) {
                        // Collision
                        schedule.interrupt(Clock.getTickNum() + tickTimeStep * step);
                        collided = true;

                        // We only break here, may need to interrupt multiple (improbable, test)
                        break;
                    }
                }
            }

            if (collided) {
                // things will be different after the interruption
                break;
            }
        }

        for (ColliderMoveSchedule schedule : schedules) {
            Collider collider = schedule.getCollider(Clock.getTickNum());
            collider.moveTo(schedule.getEntity());
            collider.rotateTo(schedule.getEntity());
        }
    }

    public static void init() {
        collisions = new Collisions();
    }

    public static void remove(ColliderMoveSchedule schedule) {
        schedules.remove(schedule);
    }

    public static boolean collisionAt(Collider collider, float dx, float dy, float drotation) {
        float oldX = collider.getX();
        float oldY = collider.getY();
        float oldRotation = collider.getRotation();
        collider.moveBy(dx, dy);
        collider.rotateBy(drotation);
        TEMP_CHUNKS2.clear();
        collider.containingChunks(TEMP_CHUNKS2);
        boolean collided = false;
        for (Chunk chunk : TEMP_CHUNKS2) {
            if (chunk.collisionTree.anyElementsIntersect(collider)) {
                collided = true;
                break;
            }
        }
        collider.moveTo(oldX, oldY);
        collider.rotateTo(oldRotation);
        return collided;
    }
}
