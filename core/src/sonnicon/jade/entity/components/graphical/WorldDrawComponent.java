package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;

import java.util.Collections;
import java.util.HashSet;

public abstract class WorldDrawComponent extends Component implements IRenderable {
    protected TextureRegion region;
    protected float width;
    protected float height;
    protected PositionComponent positionComponent;
    protected Renderer.RenderLayer layer;

    public WorldDrawComponent() {

    }

    public WorldDrawComponent(TextureRegion region, float width, float height, Renderer.RenderLayer layer) {
        setup(region, width, height, layer);
    }

    private WorldDrawComponent setup(TextureRegion region, float width, float height, Renderer.RenderLayer layer) {
        this.region = region;
        this.width = width;
        this.height = height;
        this.layer = layer;
        return this;
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
    public void render(SpriteBatch batch, float delta, Renderer.RenderLayer layer) {
        if (positionComponent != null && positionComponent.tile != null) {
            batch.draw(region, positionComponent.tile.getDrawX(), positionComponent.tile.getDrawY(), width, height);
        }
    }

    @Override
    public boolean compare(Component other) {
        if (other.getClass() != getClass()) {
            return false;
        }
        WorldDrawComponent comp = (WorldDrawComponent) other;
        return region == comp.region &&
                width == comp.width &&
                height == comp.height &&
                layer == comp.layer &&
                positionComponent.compare(((WorldDrawComponent) other).positionComponent);
    }

    @Override
    public WorldDrawComponent copy() {
        return ((WorldDrawComponent) super.copy()).setup(region, width, height, layer);
    }
}
