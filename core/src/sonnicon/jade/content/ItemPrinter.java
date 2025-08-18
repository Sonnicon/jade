package sonnicon.jade.content;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.graphical.ChunkDrawComponent;
import sonnicon.jade.entity.components.storage.EntitySizeComponent;
import sonnicon.jade.entity.components.storage.StorableComponent;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.entity.components.usage.UseFunctionComponent;
import sonnicon.jade.entity.components.weapon.ClickSwingComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.CollisionMoveAction;
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
                new StorageComponent(new EntityStorage()),
                new UseFunctionComponent((Entity user, Float x, Float y) -> {
                    user.moveTo(x, y);
                    return true;
                })
        );
        result.moveTo(location);
        return result;
    }

    public static Entity printItemRedbox(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("debug-redbox"), Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("move box", Textures.atlasFindDrawable("debug-redbox")),
                new UseFunctionComponent((Entity user, Float x, Float y) -> {
                    Actions.obtain(CollisionMoveAction.class).set(user, x, y, 0f).setDuration(3f).start();
                    return true;
                })
        );
        result.moveTo(location);
        return result;
    }

    public static Entity printWeaponDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("item-weapon"), Tile.TILE_SIZE * 0.5f, Tile.TILE_SIZE * 1.5f, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("debug weapon", Textures.atlasFindDrawable("item-weapon")),
                new ClickSwingComponent());
        result.moveTo(location);
        return result;
    }
}
