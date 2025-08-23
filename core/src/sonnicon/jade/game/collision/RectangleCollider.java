package sonnicon.jade.game.collision;

import sonnicon.jade.Jade;
import sonnicon.jade.graphics.particles.BoxParticle;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class RectangleCollider extends Collider implements IBoundRectangle {
    protected float width, height;

    public RectangleCollider(float radius) {
        this(radius * 2f, radius * 2f);
    }

    public RectangleCollider(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(
                super.debugProperties(),
                "width", getWidth(),
                "height", getHeight()
        );
    }

    @Override
    public Map<Object, Runnable> debugActions() {
        return Utils.mapFrom(
                "visualise", (Runnable) () -> {
                    BoxParticle bp1 = Jade.renderer.particles.createParticle(BoxParticle.class, getX(), getY());
                    bp1.setSize(getWidth(), getHeight(), getRotation());
                    bp1.lifetime = 10f;
                }
        );
    }
}
