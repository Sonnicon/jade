package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.utils.Pool;
import sonnicon.jade.gui.Gui;

public class TextParticle extends Particle implements Pool.Poolable {
    public BitmapFontCache cache = new BitmapFontCache(Gui.getFont());

    public TextParticle() {

    }

    public void setText(String string) {
        cache.setText(string, 0f, 0f);
    }

    @Override
    public void render(Batch batch, float delta) {
        super.render(batch, delta);

        float drawX = (float) (x + 4f * Math.sin(life + System.currentTimeMillis() / 600.));
        y += delta * 20f;
        cache.setPosition(drawX, y);
        cache.setAlphas(Math.max(1f - life / lifetime, 0f));
        cache.draw(batch);
    }
}
