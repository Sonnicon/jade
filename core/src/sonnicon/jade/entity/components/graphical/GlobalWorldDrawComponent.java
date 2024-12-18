package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;

public class GlobalWorldDrawComponent extends WorldDrawComponent {

    public GlobalWorldDrawComponent() {

    }

    public GlobalWorldDrawComponent(TextureSet textures, float width, float height, Renderer.RenderLayer layer) {
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
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (positionComponent == null || positionComponent.isInNull()) {
            return false;
        }

        // Animations don't really matter here, so floating instead of draw
        float drawX = positionComponent.getFloatingX();
        float drawY = positionComponent.getFloatingY();

        return drawX > Jade.renderer.getCameraEdgeRight() ||
                (drawX + width) < Jade.renderer.getCameraEdgeLeft() ||
                drawY > Jade.renderer.getCameraEdgeBottom() ||
                (drawY + height) < Jade.renderer.getCameraEdgeTop();
    }

    @Override
    public GlobalWorldDrawComponent copy() {
        return (GlobalWorldDrawComponent) super.copy();
    }
}
