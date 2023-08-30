package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

public class WallDrawComponent extends ChunkDrawComponent {


    private static final EventTypes.EntityMoveTileEvent moveEvent = (Entity ent, Tile source, Tile dest) -> {
        ((CachedDrawBatch) Renderer.Batch.terrain.batch).invalidate();
    };

    public WallDrawComponent() {

    }

    public WallDrawComponent(TextureSet textures) {
        super(textures, Tile.TILE_SIZE, Tile.TILE_SIZE, Renderer.RenderLayer.terrain);
    }


    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveEvent);
        moveEvent.apply(entity, null, entity.getComponent(PositionComponent.class).tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(moveEvent);
        moveEvent.apply(entity, entity.getComponent(PositionComponent.class).tile, null);
    }

    @Override
    protected void addToChunk(Chunk chunk) {
        super.addToChunk(chunk);
    }

    @Override
    public WallDrawComponent copy() {
        return (WallDrawComponent) super.copy();
    }
}
