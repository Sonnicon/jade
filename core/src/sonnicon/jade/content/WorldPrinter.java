package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.TileTraitComponent;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.graphical.FowDrawComponent;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.world.Tile;

public class WorldPrinter {
    private static final TextureSet wallTextures = new TextureSet("wall1");
    private static final TextureSet floorTextures = new TextureSet("floor1");

    public static Entity printFloorEntity(Tile location) {
        Entity floorEntity = new Entity();
        floorEntity.addComponents(
                new PositionComponent(location),
                new ChunkDrawComponent(floorTextures, Tile.TILE_SIZE, Tile.TILE_SIZE, Renderer.RenderLayer.floor));
        return floorEntity;
    }

    public static Entity printWallEntity(Tile location) {
        Entity wallEntity = new Entity();
        wallEntity.addComponents(
                new PositionComponent(location),
                new WallDrawComponent(wallTextures),
                new FowDrawComponent(),
                new TileTraitComponent(Traits.Trait.blockMovement)
        );
        return wallEntity;
    }
}
