package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.world.Tile;

public class ChunkDrawComponent extends WorldDrawComponent {

    private static final EventTypes.EntityMoveTileEvent moveHandler =
            (Entity entity, Tile from, Tile dest) -> {
                ChunkDrawComponent comp = entity.getComponentFuzzy(ChunkDrawComponent.class);

                if (from != dest) {
                    if (from != null) {
                        from.chunk.removeRenderable(comp);
                    }

                    if (dest != null) {
                        dest.chunk.addRenderable(comp, comp.layer);
                    }
                }
            };

    public ChunkDrawComponent() {

    }

    public ChunkDrawComponent(TextureSet textures, float width, float height, RenderLayer layer) {
        super(textures, width, height, layer);
    }

    public ChunkDrawComponent(TextureSet textures, float size, RenderLayer layer) {
        super(textures, size, size, layer);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        entity.events.register(moveHandler);
        moveHandler.apply(entity, null, entity.getTile());
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        moveHandler.apply(entity, entity.getTile(), null);
        entity.events.unregister(moveHandler);
    }

    @Override
    public ChunkDrawComponent copy() {
        return (ChunkDrawComponent) super.copy();
    }
}
