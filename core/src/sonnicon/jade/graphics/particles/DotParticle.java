package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;

public class DotParticle extends Particle {
    private final TextureRegion textureRegion = Textures.atlasFindRegion("icon-cross");

    public DotParticle() {

    }

    @Override
    public void render(GraphicsBatch batch, float delta) {
        super.render(batch, delta);

        SpriteBatch sb = (SpriteBatch) batch;
        float scl = 1f - (life / lifetime);
        float w = textureRegion.getRegionWidth() * scl;
        float h = textureRegion.getRegionHeight() * scl;
        sb.draw(textureRegion, x - w / 2f, y - h / 2f, w, h);
    }
}
