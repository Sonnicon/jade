package sonnicon.jade.graphics;

import sonnicon.jade.graphics.draw.GraphicsBatch;

public interface IRenderable {
    void render(GraphicsBatch batch, float delta, RenderLayer layer);

    default boolean culled(RenderLayer layer) {
        return false;
    }
}
