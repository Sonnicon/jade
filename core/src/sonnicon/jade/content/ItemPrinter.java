package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.AutoDrawComponent;
import sonnicon.jade.entity.components.ItemComponent;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class ItemPrinter {
    public static Entity printItemDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("item-debug"), 16f, 16f),
                new ItemComponent());
        return result;
    }
}
