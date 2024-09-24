package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.world.PositionBindComponent;
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
            float drawx = positionComponent.getDrawX() - width / 2f;
            float drawy = positionComponent.getDrawY() - height / 2f;
            float drawWidth = width;
            float drawHeight = height;
            TextureRegion region = textures.getDrawable().getRegion();

            AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
            if (animationComponent != null && animationComponent.isAnimating()) {
                rotation += animationComponent.getRotation();
                drawWidth *= animationComponent.getWidth();
                drawHeight *= animationComponent.getHeight();
            }

            drawx += getAnimationOffsetX();
            drawy += getAnimationOffsetY();

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

    //todo refactor these functions
    public float getAnimationOffsetX() {
        float result = 0;

        // If we're bound to something, we take that animation offset as the base
        PositionBindComponent positionBindComponent = entity.getComponent(PositionBindComponent.class);
        if (positionBindComponent != null && positionBindComponent.follow != null) {
            WorldDrawComponent fwdc = positionBindComponent.follow.getComponentFuzzy(WorldDrawComponent.class);
            if (fwdc != null) {
                 result += fwdc.getAnimationOffsetX();
            }
        }

        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent != null && animationComponent.isAnimating()) {
            result += animationComponent.getX();
        }

        return result;
    }

    public float getAnimationOffsetY() {
        float result = 0;

        // If we're bound to something, we take that animation offset as the base
        PositionBindComponent positionBindComponent = entity.getComponent(PositionBindComponent.class);
        if (positionBindComponent != null && positionBindComponent.follow != null) {
            WorldDrawComponent fwdc = positionBindComponent.follow.getComponentFuzzy(WorldDrawComponent.class);
            if (fwdc != null) {
                result += fwdc.getAnimationOffsetY();
            }
        }

        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent != null && animationComponent.isAnimating()) {
            result += animationComponent.getY();
        }

        return result;
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
