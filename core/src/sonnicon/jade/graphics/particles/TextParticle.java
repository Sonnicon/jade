package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.gui.Gui;

public class TextParticle extends Particle {
    public BitmapFontCache cache = new BitmapFontCache(Gui.getFont());
    public boolean wiggle = true;

    public TextParticle() {

    }

    public void setText(String string) {
        cache.setText(string, 0f, 0f);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        super.render(batch, delta, layer);

        if (wiggle) {
            float drawX = (float) (x + 4f * Math.sin(life + System.currentTimeMillis() / 600.));
            y += delta * 20f;
            cache.setPosition(drawX, y);
        } else {
            cache.setPosition(x, y);
        }
        cache.setAlphas(Math.max(1f - life / lifetime, 0f));
        cache.draw((SpriteBatch) batch);
    }
}
