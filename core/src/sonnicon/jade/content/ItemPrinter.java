package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.*;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class ItemPrinter {
    public static Entity printItemDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("item-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE),
                EntitySizeComponent.medium,
                new StorableComponent("debug item", Textures.atlasFindDrawable("item-debug")),
                new StorageComponent());
        return result;
    }
}
