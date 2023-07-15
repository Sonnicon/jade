package sonnicon.jade.entity.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;

import java.util.Collections;
import java.util.HashSet;

public class TileDrawComponent extends Component implements IRenderable {
    protected TextureRegion region;
    protected float width;
    protected float height;
    protected PositionComponent positionComponent;

    public TileDrawComponent() {

    }

    public TileDrawComponent(TextureRegion region, float width, float height) {
        setup(region, width, height);
    }

    private TileDrawComponent setup(TextureRegion region, float width, float height) {
        this.region = region;
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public boolean canAddToEntity(Entity entity) {
        return entity.components.containsKey(PositionComponent.class);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        positionComponent = entity.getComponent(PositionComponent.class);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return new HashSet<>(Collections.singletonList(PositionComponent.class));
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
        TileDrawComponent comp = (TileDrawComponent) other;
        return region == comp.region &&
                width == comp.width &&
                height == comp.height &&
                positionComponent.compare(((TileDrawComponent) other).positionComponent);
    }

    @Override
    public TileDrawComponent copy() {
        return ((TileDrawComponent) super.copy()).setup(region, width, height);
    }
}
