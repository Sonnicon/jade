package sonnicon.jade.graphics.draw.ifaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface IDrawRotated {
    void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height,
              float scaleX, float scaleY, float rotation);

    default void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height,
                      float rotation) {
        draw(region, x, y, originX, originY, width, height, 1f, 1f, -rotation);
    }

    default void draw(TextureRegion region, float x, float y, float width, float height,
                      float rotation) {
        draw(region, x, y, 0 + width / 2f, 0 + height / 2f, width, height, 1f, 1f, -rotation);
    }
}
