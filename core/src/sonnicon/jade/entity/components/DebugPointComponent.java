package sonnicon.jade.entity.components;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;

public class DebugPointComponent extends Component implements IRenderable {

    public DebugPointComponent() {
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Jade.renderer.addRenderable(this, RenderLayer.overfow);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Jade.renderer.removeRenderable(this);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, RenderLayer layer) {
        SpriteBatch b = (SpriteBatch) batch;
        b.draw(Textures.atlasFindRegion("debugPoint"),
                entity.getX() - 1f, entity.getY() - 1f,
                2f, 2f);
    }
}
