package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

public class ChunkDrawComponent extends WorldDrawComponent {

    private transient Chunk currentChunk;
    private static final EventTypes.EntityMoveTileEvent moveHandler =
            (Entity entity, Tile source, Tile destination) -> {
                ChunkDrawComponent comp = entity.getComponentFuzzy(ChunkDrawComponent.class);
                Chunk c = destination == null ? null : destination.chunk;

                if (c != comp.currentChunk) {
                    if (comp.currentChunk != null) {
                        comp.removeFromChunk();
                    }

                    if (c != null) {
                        comp.addToChunk(c);
                    }
                }
            };

    public ChunkDrawComponent() {

    }

    public ChunkDrawComponent(TextureSet textures, float width, float height, Renderer.RenderLayer layer) {
        super(textures, width, height, layer);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveHandler);
        if (positionComponent.tile != null) {
            addToChunk(positionComponent.tile.chunk);
        }
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveHandler);
        if (currentChunk != null) {
            removeFromChunk();
        }
    }

    protected void addToChunk(Chunk chunk) {
        currentChunk = chunk;
        chunk.addRenderable(this, layer);
    }

    protected void removeFromChunk() {
        currentChunk.removeRenderable(this);
    }

    @Override
    public boolean compare(IComparable other) {
        if (other.getClass() != getClass()) {
            return false;
        }
        ChunkDrawComponent comp = (ChunkDrawComponent) other;
        return textures == comp.textures &&
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
