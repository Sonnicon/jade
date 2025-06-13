package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.graphics.draw.ifaces.IDrawRegular;
import sonnicon.jade.graphics.draw.ifaces.IDrawRotated;

public class SpriteBatch extends com.badlogic.gdx.graphics.g2d.SpriteBatch implements GraphicsBatch, IDrawRegular, IDrawRotated {

    public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX,
                     float scaleY, float rotation, boolean flipX, boolean flipY) {
        draw(region.getTexture(),
                x, y,
                originX, originY,
                width, height,
                scaleX, scaleY,
                -rotation,
                region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), region.getRegionHeight(),
                flipX, flipY);
    }
}
