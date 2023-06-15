package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.TileDrawComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class WorldPrinter {

    public static Entity printFloorEntity(Tile location) {
        Entity floorEntity = new Entity();
        floorEntity.addComponents(
                new PositionComponent(location),
                new TileDrawComponent(Textures.atlasFindRegion(Math.random() > 0.2 ? "floor1" : "wall1"), 16, 16));
        return floorEntity;
    }
}
