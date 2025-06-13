package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.DebugComponent;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.storage.EntitySizeComponent;
import sonnicon.jade.entity.components.storage.StorableComponent;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.entity.components.world.PositionRelativeComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.world.Tile;

public class ItemPrinter {
    public static Entity printItemDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("item-debug"), Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("debug item", Textures.atlasFindDrawable("item-debug")),
                new StorageComponent(new EntityStorage()));
        result.forceMoveTo(location);
        return result;
    }

    public static Entity printWeaponDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("item-weapon"), Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("debug weapon", Textures.atlasFindDrawable("item-weapon")),
                new DebugComponent(),
                new PositionRelativeComponent().bindToEntity(PlayerControlComponent.getEntity()));
        result.forceMoveTo(location);
        return result;
    }
}
