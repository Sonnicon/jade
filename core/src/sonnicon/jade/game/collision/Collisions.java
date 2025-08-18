package sonnicon.jade.game.collision;

import sonnicon.jade.Jade;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.particles.CrossParticle;
import sonnicon.jade.world.Chunk;

import java.util.ArrayList;

public class Collisions implements Clock.IOnTick {
    protected static ArrayList<ColliderMoveSchedule> schedules = new ArrayList<>();

    private static final float TICK_STEP_SIZE = 0.15f;
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

        float timeToNextTick = Clock.getTimeToNextTick();
        int numSteps = (int) Math.ceil(timeToNextTick / TICK_STEP_SIZE);
        float tickTimeStep = timeToNextTick / numSteps;
        for (int step = 0; step < numSteps; step++) {
            if (schedules.isEmpty()) {
                return;
            }

            float clockTime = Clock.getTickNum() + tickTimeStep * step;

            // Move everything
            for (ColliderMoveSchedule schedule : schedules) {
                Collider collider = schedule.getCollider(clockTime);
                collider.moveTo(schedule.getX(clockTime), schedule.getY(clockTime));
                Jade.renderer.particles.createParticle(CrossParticle.class, collider.getX(), collider.getY()).scale = 0.2f;
            }

            boolean collided = false;
            for (ColliderMoveSchedule schedule : schedules) {
                Collider collider = schedule.getCollider(clockTime);
                TEMP_CHUNKS2.clear();
                collider.containingChunks(TEMP_CHUNKS2);
                for (Chunk chunk : TEMP_CHUNKS2) {
                    if (chunk.collisionTree.anyElementsIntersect(collider)) {
                        // Collision
                        schedule.interrupt(Clock.getTickNum() + tickTimeStep * Math.max(0, step - 1) - 0.0001f);
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
            collider.moveTo(schedule.getX(Clock.getTickNum()), schedule.getY(Clock.getTickNum()));
        }
    }

    public static void init() {
        collisions = new Collisions();
    }

    public static void remove(ColliderMoveSchedule schedule) {
        schedules.remove(schedule);
    }
}
