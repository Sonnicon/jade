package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;

public class GlobalDrawComponent extends WorldDrawComponent {

    public GlobalDrawComponent() {

    }

    public GlobalDrawComponent(TextureSet textures, float width, float height, Renderer.RenderLayer layer) {
        super(textures, width, height, layer);
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
    public boolean culled(Renderer.RenderLayer layer) {
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
