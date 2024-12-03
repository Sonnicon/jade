package sonnicon.jade.entity.components.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.graphical.WorldDrawComponent;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.ifaces.IDrawRegular;
import sonnicon.jade.util.Utils;

import java.util.HashSet;
import java.util.Map;

public class HitboxComponent extends Component implements IRenderable {
    public short sizeTotal, fieldCount;

    private static final TextureRegion DAMAGEFIELD_TEXTURE = Textures.atlasFindRegion("damagefield");

    public HitboxComponent() {

    }

    public HitboxComponent(short sizeTotal, short fieldCount) {
        setup(sizeTotal, fieldCount);
    }

    private HitboxComponent setup(short size, short count) {
        this.sizeTotal = size;
        this.fieldCount = count;
        return this;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.getComponentFuzzy(WorldDrawComponent.class).addJoined(this);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.getComponentFuzzy(WorldDrawComponent.class).removeJoined(this);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(WorldDrawComponent.class, PositionComponent.class);
    }

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        //todo not render when not needed
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
        if (positionComponent != null && positionComponent.isInNull()) {
            IDrawRegular b = (IDrawRegular) batch;

            float drawSize = (float) sizeTotal / fieldCount;
            float drawx = positionComponent.getFloatingX() - sizeTotal / 2f;
            float drawy = positionComponent.getFloatingY() - sizeTotal / 2f;

            for (int ix = 0, iy = 0; iy < fieldCount; iy += ++ix / fieldCount, ix %= fieldCount) {
                b.draw(
                        DAMAGEFIELD_TEXTURE,
                        drawx + drawSize * ix,
                        drawy + drawSize * iy,
                        drawSize, drawSize);
            }
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "size", sizeTotal, "count", fieldCount);
    }

    @Override
    public HitboxComponent copy() {
        return ((HitboxComponent) super.copy()).setup(sizeTotal, fieldCount);
    }
}
