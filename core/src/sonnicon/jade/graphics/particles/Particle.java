package sonnicon.jade.graphics.particles;

import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.Map;

public abstract class Particle implements IDebuggable, ObjectPool.IPooledObject, IRenderable {
    public float timeCreated = 0f;
    public float lifetime = 1f;

    public float x, y;

    public void create(float x, float y) {
        this.x = x;
        this.y = y;

        timeCreated = Clock.getTickNum();
    }

    public abstract void render(GraphicsBatch batch, float delta, RenderLayer layer);

    public float getProgress() {
        return Math.min((Clock.getTickNum() - timeCreated) / lifetime, 1f);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("timeCreated", timeCreated, "lifetime", lifetime, "x", x, "y");
    }
}
