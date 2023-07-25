package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.Renderer;

public class GlobalDrawComponent extends WorldDrawComponent {

    public GlobalDrawComponent() {

    }

    public GlobalDrawComponent(TextureRegion region, float width, float height, Renderer.RenderLayer layer) {
        super(region, width, height, layer);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Jade.renderer.addRenderable(this, layer);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Jade.renderer.removeRenderable(this);
    }


    @Override
    public boolean culled() {
        if (positionComponent == null || positionComponent.tile == null) {
            return false;
        }

        float drawX = positionComponent.tile.getDrawX();
        float drawY = positionComponent.tile.getDrawY();

        return drawX > Jade.renderer.getCameraEdgeRight() ||
                (drawX + width) < Jade.renderer.getCameraEdgeLeft() ||
                drawY > Jade.renderer.getCameraEdgeBottom() ||
                (drawY + height) < Jade.renderer.getCameraEdgeTop();
    }

    @Override
    public GlobalDrawComponent copy() {
        return (GlobalDrawComponent) super.copy();
    }
}
