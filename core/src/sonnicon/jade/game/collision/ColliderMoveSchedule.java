package sonnicon.jade.game.collision;

import sonnicon.jade.entity.Entity;

public interface ColliderMoveSchedule {
    Entity getEntity();

    float getDeltaX(float tickNum);

    float getDeltaY(float tickNum);

    float getDeltaRotation(float tickNum);

    // If using multiple colliders, be careful with positions changing
    Collider getCollider(float tickNum);

    void interrupt(float tickNum);

    boolean isMoving();
}
