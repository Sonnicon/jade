package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface IRegularDraw {
    void draw(TextureRegion region, float x, float y);

    void draw(TextureRegion region, float x, float y, float width, float height);
}
