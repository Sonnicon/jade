package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.AutoDrawComponent;
import sonnicon.jade.entity.components.KeyboardMovementComponent;
import sonnicon.jade.entity.components.PositionComponent;
import sonnicon.jade.entity.components.StorageComponent;
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
                new AutoDrawComponent(Textures.atlasFindRegion("character-debug"), 16f, 16f),
                new StorageComponent(entityStorage),
                new KeyboardMovementComponent());

        return result;
    }
}
