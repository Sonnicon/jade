package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.*;
import sonnicon.jade.game.EntitySize;
import sonnicon.jade.game.LimitedEntityStorage;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class CharacterPrinter {
    public static Entity printCharacterPlayer(Tile location) {
        Entity result = new Entity();

        LimitedEntityStorage entityStorage = new LimitedEntityStorage().
                addSlot(EntitySize.tiny, EntitySize.huge, 1, LimitedEntityStorage.SlotType.hand).
                addSlot(EntitySize.tiny, EntitySize.huge, 1, LimitedEntityStorage.SlotType.hand).
                addSlot(EntitySize.tiny, EntitySize.large, 2, LimitedEntityStorage.SlotType.generic);
        entityStorage.capacity = EntitySize.huge.value * 4;


        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("character-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE),
                new StorageComponent(entityStorage),
                new KeyboardMovementComponent(),
                new PlayerControlComponent());

        return result;
    }
}
