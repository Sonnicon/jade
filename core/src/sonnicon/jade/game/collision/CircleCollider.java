package sonnicon.jade.game.collision;

public class CircleCollider extends Collider implements IBoundCircle {
    protected float radius;

    @Override
    public float getRadius() {
        return radius;
    }
}
