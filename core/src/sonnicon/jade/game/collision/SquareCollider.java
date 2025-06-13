package sonnicon.jade.game.collision;

public class SquareCollider extends Collider implements IBoundSquare {
    protected float radius;

    public SquareCollider(float radius) {
        this.radius = radius;
    }

    @Override
    public float getRadius() {
        return radius;
    }
}
