package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.Renderer;

public class AutoDrawComponent extends TileDrawComponent {

    public AutoDrawComponent() {

    }

    public AutoDrawComponent(TextureRegion region, float width, float height, Renderer.RenderLayer layer) {
        super(region, width, height, layer);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Renderer.addRenderable(this, layer);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Renderer.removeRenderable(this);
    }
}
