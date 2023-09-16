package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.utils.Pool;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.Map;

public abstract class Particle implements Pool.Poolable, IDebuggable {
    public float life = 0f;
    public float lifetime = 1f;
    public boolean destroy = false;

    public float x, y;

    public void create(float x, float y) {
        this.x = x;
        this.y = y;

        life = 0f;
        destroy = false;
    }

    public void render(GraphicsBatch batch, float delta) {
        advanceLife(delta);
    }

    public void tick(float delta) {
    }

    protected void advanceLife(float delta) {
        life += delta;
        if (life > lifetime) {
            destroy = true;
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("life", life, "lifetime", lifetime, "x", x, "y");
    }
}
