package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.DebugComponent;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.graphical.FowDrawComponent;
import sonnicon.jade.entity.components.graphical.WallDrawComponent;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.entity.components.world.TileTraitComponent;
import sonnicon.jade.game.collision.SquareCollider;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.world.Tile;

public class WorldPrinter {
    private static final TextureSet wallTextures = new TextureSet("debug");
    private static final TextureSet floorTextures = new TextureSet("debug2");

    public static Entity printFloorEntity(Tile location) {
        Entity floorEntity = new Entity();
        floorEntity.addComponents(
                new DebugComponent(),
                new ChunkDrawComponent(floorTextures, Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.floor));
        floorEntity.forceMoveTo(location);
        return floorEntity;
    }

    public static Entity printWallEntity(Tile location) {
        Entity wallEntity = new Entity();
        wallEntity.addComponents(
                new DebugComponent(),
                new WallDrawComponent(wallTextures),
                new FowDrawComponent(),
                new CollisionComponent(new SquareCollider(16f)),
                new TileTraitComponent(Traits.Trait.blockMovement)
        );
        wallEntity.forceMoveTo(location);
        return wallEntity;
    }

    public static Entity printRedboxEntity(Tile location) {
        Entity redboxEntity = new Entity();
        redboxEntity.addComponents(
                new ChunkDrawComponent(new TextureSet("debug-redbox"), 16f, RenderLayer.objects),
                new CollisionComponent(new SquareCollider(8f))
        );
        redboxEntity.forceMoveTo(location);
        return redboxEntity;
    }
}
