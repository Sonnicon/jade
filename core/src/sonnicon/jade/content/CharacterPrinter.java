package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.AutoDrawComponent;
import sonnicon.jade.entity.components.KeyboardMovementComponent;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class CharacterPrinter {
    public static Entity printCharacterPlayer(Tile location) {
        Entity result = new Entity();
        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("character-debug"), 16f, 16f),
                new KeyboardMovementComponent());
        return result;
    }
}
