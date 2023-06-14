package sonnicon.jade.game.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.game.Entity;
import sonnicon.jade.graphics.Renderable;
import sonnicon.jade.graphics.Renderer;

import java.util.Collections;
import java.util.HashSet;

public class DrawComponent extends Component implements Renderable {
    protected TextureRegion region;
    protected float width;
    protected float height;
    protected PositionComponent positionComponent;

    public DrawComponent(TextureRegion region, float width, float height) {
        this.region = region;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean canAddToEntity(Entity entity) {
        return entity.components.containsKey(PositionComponent.class);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        positionComponent = (PositionComponent) entity.components.get(PositionComponent.class);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return new HashSet<>(Collections.singletonList(PositionComponent.class));
    }

    @Override
    public Component copy() {
        return new DrawComponent(region, width, height);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (positionComponent != null && positionComponent.tile != null) {
            batch.draw(region, positionComponent.tile.getDrawX(), positionComponent.tile.getDrawY(), width, height);
        }
    }

    @Override
    public boolean culled() {
        if (positionComponent == null || positionComponent.tile == null) {
            return false;
        }

        float drawX = positionComponent.tile.getDrawX();
        float drawY = positionComponent.tile.getDrawY();

        return drawX > Renderer.getCameraEdgeRight() ||
                (drawX + width) < Renderer.getCameraEdgeLeft() ||
                drawY > Renderer.getCameraEdgeBottom() ||
                (drawY + height) < Renderer.getCameraEdgeTop();
    }

    @Override
    public boolean compare(Component other) {
        if (other.getClass() != getClass()) {
            return false;
        }
        DrawComponent comp = (DrawComponent) other;
        return region == comp.region &&
                width == comp.width &&
                height == comp.height &&
                positionComponent.compare(((DrawComponent) other).positionComponent);
    }
}
