package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import sonnicon.jade.gui.Gui;

public class TextParticle extends Particle implements Pool.Poolable {
    public String text = "";

    public TextParticle() {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);

        x += delta;
        y += delta * 8f;

        Gui.getFont().setColor(1, 1, 1,  1f - life / lifetime);
        Gui.getFont().draw(batch, text, x, y);
    }
}
