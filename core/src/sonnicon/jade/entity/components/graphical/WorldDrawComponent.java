package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.IRegularDraw;
import sonnicon.jade.util.IComparable;

import java.util.Collections;
import java.util.HashSet;

public abstract class WorldDrawComponent extends Component implements IRenderable {
    protected TextureSet textures;
    protected float width;
    protected float height;
    protected PositionComponent positionComponent;
    protected Renderer.RenderLayer layer;

    public WorldDrawComponent() {

    }

    public WorldDrawComponent(TextureSet textures, float width, float height, Renderer.RenderLayer layer) {
        setup(textures, width, height, layer);
    }

    private WorldDrawComponent setup(TextureSet textures, float width, float height, Renderer.RenderLayer layer) {
        this.textures = textures;
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
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        if (positionComponent != null && positionComponent.tile != null) {
            IRegularDraw b = (IRegularDraw) batch;
            b.draw(textures.getDrawable().getRegion(), positionComponent.tile.getDrawX(), positionComponent.tile.getDrawY(), width, height);
        }
    }

    @Override
    public boolean compare(IComparable other) {
        if (!(other instanceof WorldDrawComponent)) {
            return false;
        }
        WorldDrawComponent comp = (WorldDrawComponent) other;
        return textures == ((WorldDrawComponent) other).textures &&
                width == comp.width &&
                height == comp.height &&
                layer == comp.layer &&
                positionComponent.compare(((WorldDrawComponent) other).positionComponent);
    }

    @Override
    public WorldDrawComponent copy() {
        return ((WorldDrawComponent) super.copy()).setup(textures, width, height, layer);
    }
}
