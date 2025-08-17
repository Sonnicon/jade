package sonnicon.jade.graphics.particles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;

public class CrossParticle extends Particle {
    private final TextureRegion textureRegion = Textures.atlasFindRegion("icon-cross");
    public float scale = 1f;

    public CrossParticle() {

    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        super.render(batch, delta, layer);

        SpriteBatch sb = (SpriteBatch) batch;
        float scl = scale * (1f - (life / lifetime));
        float w = textureRegion.getRegionWidth() * scl;
        float h = textureRegion.getRegionHeight() * scl;
        sb.draw(textureRegion, x - w / 2f, y - h / 2f, w, h);
    }

    @Override
    public void onObtained() {
        super.onObtained();
        scale = 1f;
    }
}
