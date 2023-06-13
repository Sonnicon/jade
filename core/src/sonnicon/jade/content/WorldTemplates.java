package sonnicon.jade.content;

import sonnicon.jade.game.Entity;
import sonnicon.jade.game.components.AutoDrawComponent;
import sonnicon.jade.game.components.PositionComponent;
import sonnicon.jade.game.components.DrawComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class WorldTemplates {

    public static Entity createFloorEntity(Tile location) {
        Entity floorEntity = new Entity();
        floorEntity.addComponents(
                new PositionComponent(location),
                new DrawComponent(Textures.atlasFindRegion(Math.random() > 0.2 ? "floor1" : "wall1"), 16, 16));
        return floorEntity;
    }
}
