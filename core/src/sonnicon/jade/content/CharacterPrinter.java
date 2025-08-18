package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.DebugComponent;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.player.KeyboardMovementComponent;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.storage.CharacterStorageComponent;
import sonnicon.jade.entity.components.world.CollisionComponent;
import sonnicon.jade.game.EntitySize;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.FixedSlotEntityStorage;
import sonnicon.jade.game.collision.SquareCollider;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.world.Tile;

public class CharacterPrinter {
    public static Entity printCharacterPlayer(Tile location) {
        Entity result = printCharacterEnemy(location);

        FixedSlotEntityStorage storage = new FixedSlotEntityStorage();
        CharacterStorageComponent storageComponent = new CharacterStorageComponent(storage);

        addHand(storageComponent, EntitySize.tiny, EntitySize.huge, 1);
        addHand(storageComponent, EntitySize.tiny, EntitySize.huge, 1);

        addSlot(storage, EntitySize.tiny, EntitySize.huge, 1);
        addSlot(storage, EntitySize.tiny, EntitySize.huge, 4);
        addSlot(storage, EntitySize.tiny, EntitySize.large, 2);

        storage.capacity = EntitySize.huge.value * 40;

        result.addComponents(
                storageComponent,
                new KeyboardMovementComponent());
        PlayerControlComponent.setControlledEntity(result);

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

    public static Entity printCharacterEnemy(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new DebugComponent(),
                new ChunkDrawComponent(new TextureSet("character-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                new CollisionComponent(new SquareCollider(14f)));
        result.addTrait(Traits.Trait.blockMovement);
        result.moveTo(location);
        return result;
    }
}
