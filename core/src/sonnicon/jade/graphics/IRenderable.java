package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface IRenderable {
    void render(Batch batch, float delta, Renderer.RenderLayer layer);

    default boolean culled(Renderer.RenderLayer layer) {
        return false;
    }
}
