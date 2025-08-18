package sonnicon.jade.game.collision;

public interface ColliderMoveSchedule {
    float getX(float tickNum);

    float getY(float tickNum);

    float getRotation(float tickNum);

    // If using multiple colliders, be careful with positions changing
    Collider getCollider(float tickNum);

    void interrupt(float tickNum);

    boolean isMoving();
}
