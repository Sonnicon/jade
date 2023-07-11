package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.*;
import sonnicon.jade.game.EntitySize;
import sonnicon.jade.game.FixedSlotEntityStorage;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class CharacterPrinter {
    public static Entity printCharacterPlayer(Tile location) {
        Entity result = new Entity();

        FixedSlotEntityStorage entityStorage = new FixedSlotEntityStorage().
                addSlot(EntitySize.tiny, EntitySize.huge, 1, null).
                addSlot(EntitySize.tiny, EntitySize.huge, 1, null).
                addSlot(EntitySize.tiny, EntitySize.huge, 4, null).
                addSlot(EntitySize.tiny, EntitySize.large, 2, null);
        entityStorage.capacity = EntitySize.huge.value * 40;

        for (int i = 0; i < 20; i++) {
            entityStorage.addSlot(EntitySize.huge, EntitySize.huge, 1, null);
        }


        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("character-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE),
                new StorageComponent(entityStorage),
                new KeyboardMovementComponent(),
                new PlayerControlComponent());

        return result;
    }
}
