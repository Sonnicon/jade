package sonnicon.jade.graphics.draw.ifaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface IDrawRegular {
    void draw(TextureRegion region, float x, float y);

    void draw(TextureRegion region, float x, float y, float width, float height);
}
