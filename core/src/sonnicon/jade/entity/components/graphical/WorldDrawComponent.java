package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.IRegularDraw;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public abstract class WorldDrawComponent extends Component implements IRenderable {
    protected TextureSet textures;
    protected float width;
    protected float height;

    protected PositionComponent positionComponent;
    protected Renderer.RenderLayer layer;
    protected ArrayList<IRenderable> joinedRenderables;

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
        return Utils.setFrom(PositionComponent.class);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        if (textures != null && positionComponent != null && !positionComponent.isInNull()) {
            IRegularDraw b = (IRegularDraw) batch;
            b.draw(
                    textures.getDrawable().getRegion(),
                    positionComponent.getDrawX() - width / 2f,
                    positionComponent.getDrawY() - height / 2f,
                    width, height);
        }

        if (joinedRenderables != null) {
            joinedRenderables.forEach(r -> r.render(batch, delta, layer));
        }
    }

    public void addJoined(IRenderable renderable) {
        if (joinedRenderables == null) {
            joinedRenderables = new ArrayList<>();
        }
        joinedRenderables.add(renderable);
    }

    public void removeJoined(IRenderable renderable) {
        if (joinedRenderables != null) {
            joinedRenderables.remove(renderable);
        }
    }

    @Override
    public boolean compare(IComparable other) {
        // Comparing this is unnecessary, drawing should be changed by other components which will be compared
        return true;
    }

    @Override
    public WorldDrawComponent copy() {
        return ((WorldDrawComponent) super.copy()).setup(textures, width, height, layer);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "textures", textures, "width", width, "height", height, "position", positionComponent, "layer", layer);
    }
}
