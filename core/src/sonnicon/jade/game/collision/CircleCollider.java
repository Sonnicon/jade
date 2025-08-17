package sonnicon.jade.game.collision;

public class CircleCollider extends Collider implements IBoundCircle {
    protected float radius;

    public CircleCollider(float radius) {
        this.radius = radius;
    }

    @Override
    public float getRadius() {
        return radius;
    }
}
