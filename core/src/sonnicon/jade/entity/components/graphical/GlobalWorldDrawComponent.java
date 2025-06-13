package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;

public class GlobalWorldDrawComponent extends WorldDrawComponent {

    public GlobalWorldDrawComponent() {

    }

    public GlobalWorldDrawComponent(TextureSet textures, float width, float height, RenderLayer layer) {
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
    public boolean culled(RenderLayer layer) {
        if (entity == null) {
            return false;
        }

        float drawX = entity.getX();
        float drawY = entity.getY();

        return drawX - width / 2f > Jade.renderer.getCameraEdgeRight() ||
                drawX + width / 2f < Jade.renderer.getCameraEdgeLeft() ||
                drawY - height / 2f > Jade.renderer.getCameraEdgeBottom() ||
                drawY + height / 2f < Jade.renderer.getCameraEdgeTop();
    }

    @Override
    public GlobalWorldDrawComponent copy() {
        return (GlobalWorldDrawComponent) super.copy();
    }
}
