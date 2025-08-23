package sonnicon.jade.graphics.particles;

import sonnicon.jade.Jade;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ParticleEngine implements IRenderable, IDebuggable {
    private final ArrayList<Particle> particles;

    public ParticleEngine(Renderer renderer) {
        particles = new ArrayList<>();
        renderer.addRenderable(this, RenderLayer.particles);
        renderer.addRenderable(this, RenderLayer.gui);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.render(batch, delta, layer);
            if (p.getProgress() >= 1f) {
                iter.remove();
                ObjectPool.free(p);
            }
        }
    }

    public <T extends Particle> T createParticle(Class<T> type, float x, float y) {
        T p = ObjectPool.obtain(type);
        p.create(x, y);
        particles.add(p);
        return p;
    }

    public void dispose() {
        particles.forEach(ObjectPool::free);
        particles.clear();
        Jade.renderer.removeRenderable(this);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("particles", particles);
    }
}
