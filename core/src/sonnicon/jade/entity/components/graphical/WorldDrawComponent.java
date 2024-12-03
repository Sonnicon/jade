package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.ifaces.IDrawRegular;
import sonnicon.jade.graphics.draw.ifaces.IDrawRotated;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public abstract class WorldDrawComponent extends Component implements IRenderable {
    protected TextureSet textures;
    protected float width;
    protected float height;

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
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(PositionComponent.class);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (textures != null && positionComponent != null && !positionComponent.isInNull()) {
            float rotation = positionComponent.getRotation();
            float drawWidth = width;
            float drawHeight = height;
            TextureRegion region = textures.getDrawable().getRegion();

            AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
            if (animationComponent != null && animationComponent.isAnimating()) {
                drawWidth *= animationComponent.getWidth();
                drawHeight *= animationComponent.getHeight();
            }

            rotation += AnimationComponent.getNestedRotation(entity);

            float drawx = positionComponent.getDrawX() - drawWidth / 2f;
            float drawy = positionComponent.getDrawY() - drawHeight / 2f;

            if (rotation == 0f) {
                ((IDrawRegular) batch).draw(region, drawx, drawy, drawWidth, drawHeight);
            } else {
                //todo default rotation angle
                ((IDrawRotated) batch).draw(region, drawx, drawy, drawWidth, drawHeight, 45f - rotation);
            }
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
        return Utils.mapExtendFrom(super.debugProperties(), "textures", textures, "width", width, "height", height, "layer", layer);
    }
}
