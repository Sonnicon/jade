package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.*;
import sonnicon.jade.game.EntitySize;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.FixedSlotEntityStorage;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class CharacterPrinter {
    public static Entity printCharacterPlayer(Tile location) {
        Entity result = new Entity();

        FixedSlotEntityStorage storage = new FixedSlotEntityStorage();
        CharacterStorageComponent storageComponent = new CharacterStorageComponent(storage);

        addHand(storageComponent, EntitySize.tiny, EntitySize.huge, 1);
        addHand(storageComponent, EntitySize.tiny, EntitySize.huge, 1);

        addSlot(storage, EntitySize.tiny, EntitySize.huge, 1);
        addSlot(storage, EntitySize.tiny, EntitySize.huge, 4);
        addSlot(storage, EntitySize.tiny, EntitySize.large, 2);

        storage.capacity = EntitySize.huge.value * 40;


        result.addComponents(new PositionComponent(location),
                new AutoDrawComponent(Textures.atlasFindRegion("character-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE),
                storageComponent,
                new KeyboardMovementComponent(),
                new PlayerControlComponent());

        return result;
    }

    private static FixedSlotEntityStorage addSlot(FixedSlotEntityStorage storage,
                                                  EntitySize minimumSize, EntitySize maximumSize, int maximumAmount) {
        storage.addSlot(minimumSize, maximumSize, maximumAmount, null);
        return storage;
    }

    private static void addHand(CharacterStorageComponent characterStorageComponent,
                                EntitySize minimumSize, EntitySize maximumSize, int maximumAmount) {
        EntityStorageSlot slot = ((FixedSlotEntityStorage) characterStorageComponent.storage).
                addSlot(minimumSize, maximumSize, maximumAmount, null);
        characterStorageComponent.addHand(slot);
    }
}
