package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.utils.Pools;
import sonnicon.jade.Jade;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ParticleEngine implements IRenderable, Clock.ITicking, IDebuggable {
    private final ArrayList<Particle> particles;

    public ParticleEngine(Renderer renderer) {
        particles = new ArrayList<>();
        renderer.addRenderable(this, Renderer.RenderLayer.top);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.render(batch, delta);
            if (p.destroy) {
                iter.remove();
                Pools.free(p);
            }
        }
    }

    @Override
    public void tick(float delta) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.tick(delta);
            if (p.destroy) {
                iter.remove();
                Pools.free(p);
            }
        }
    }

    public <T extends Particle> T createParticle(Class<T> type, float x, float y) {
        T p = Pools.obtain(type);
        p.create(x, y);
        particles.add(p);
        return p;
    }

    public void dispose() {
        particles.forEach(Pools::free);
        particles.clear();
        Jade.renderer.removeRenderable(this);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("particles", particles);
    }
}
