package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IRenderable {
    void render(SpriteBatch batch, float delta, Renderer.RenderLayer layer);

    default boolean culled() {
        return false;
    }
}
