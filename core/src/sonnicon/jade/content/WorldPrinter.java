package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.graphical.AutoDrawComponent;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class WorldPrinter {

    public static Entity printFloorEntity(Tile location) {
        Entity floorEntity = new Entity();
        floorEntity.addComponents(
                new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion(Math.random() > 0.2 ? "floor1" : "wall1"), Tile.TILE_SIZE, Tile.TILE_SIZE, Renderer.RenderLayer.floor));
        return floorEntity;
    }
}
