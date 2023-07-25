package sonnicon.jade.entity.components.graphical;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

import java.util.function.Consumer;

public class ChunkDrawComponent extends WorldDrawComponent {

    private transient Chunk currentChunk;
    private final transient Consumer<Object[]> moveHandler = objs -> {
        //todo make the event parameters typed
        Entity e = (Entity) objs[0];
        ChunkDrawComponent comp = e.getComponent(ChunkDrawComponent.class);

        Tile destinationTile = (Tile) objs[1];
        Chunk c = destinationTile == null ? null : destinationTile.chunk;

        if (c != comp.currentChunk) {
            if (currentChunk != null) {
                comp.removeFromChunk();
            }

            if (c != null) {
                comp.addToChunk(c);
            }
        }
    };

    public ChunkDrawComponent() {

    }

    public ChunkDrawComponent(TextureRegion region, float width, float height, Renderer.RenderLayer layer) {
        super(region, width, height, layer);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(PositionComponent.EntityMoveEvent.class, moveHandler);
        if (positionComponent.tile != null && positionComponent.tile.chunk != null) {
            addToChunk(positionComponent.tile.chunk);
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(PositionComponent.EntityMoveEvent.class, moveHandler);
    }

    protected void addToChunk(Chunk chunk) {
        currentChunk = chunk;
        chunk.addRenderable(this, layer);
    }

    protected void removeFromChunk() {
        currentChunk.removeRenderable(this);
    }

    @Override
    public boolean compare(Component other) {
        if (other.getClass() != getClass()) {
            return false;
        }
        ChunkDrawComponent comp = (ChunkDrawComponent) other;
        return region == comp.region &&
                width == comp.width &&
                height == comp.height &&
                layer == comp.layer &&
                positionComponent.compare(((ChunkDrawComponent) other).positionComponent);
    }

    @Override
    public ChunkDrawComponent copy() {
        return (ChunkDrawComponent) super.copy();
    }
}
