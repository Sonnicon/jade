package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pools;
import sonnicon.jade.Jade;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;

import java.util.ArrayList;
import java.util.Iterator;

public class ParticleEngine implements IRenderable, Clock.ITicking {
    private final ArrayList<Particle> particles;

    public ParticleEngine(Renderer renderer) {
        particles = new ArrayList<>();
        renderer.addRenderable(this, Renderer.RenderLayer.top);
    }

    @Override
    public void render(SpriteBatch batch, float delta, Renderer.RenderLayer layer) {
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
}
