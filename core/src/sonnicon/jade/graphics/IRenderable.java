package sonnicon.jade.graphics;

import sonnicon.jade.graphics.draw.GraphicsBatch;

public interface IRenderable {
    void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer);

    default boolean culled(Renderer.RenderLayer layer) {
        return false;
    }
}
