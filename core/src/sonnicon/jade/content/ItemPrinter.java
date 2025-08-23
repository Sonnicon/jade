package sonnicon.jade.content;

import com.badlogic.gdx.graphics.Color;
import sonnicon.jade.Jade;
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
import sonnicon.jade.game.collision.Collider;
import sonnicon.jade.game.collision.RectangleCollider;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.TextureSet;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.particles.CrossParticle;
import sonnicon.jade.graphics.particles.LineParticle;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.Tile;

public class ItemPrinter {
    public static Entity printItemDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("item-debug"),
                        Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("teleporter box", "Teleport user to clicked position. Is also a box.",
                        Textures.atlasFindDrawable("item-debug")),
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
                new ChunkDrawComponent(new TextureSet("debug-redbox"),
                        Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("collision move to", "move to somewhere in 3f time",
                        Textures.atlasFindDrawable("debug-redbox")),
                new UseFunctionComponent((Entity user, Float x, Float y) -> {
                    Actions.obtain(CollisionMoveAction.class)
                            .set(user, x - user.getX(), y - user.getY(), 0f)
                            .setDuration(3f).start();
                    LineParticle lp = Jade.renderer.particles.createParticle(LineParticle.class,
                            user.getX(), user.getY());
                    lp.color = Color.TEAL;
                    lp.destX = x;
                    lp.destY = y;
                    return true;
                })
        );
        result.moveTo(location);
        return result;
    }

    public static Entity printWeaponDebug(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("item-weapon"),
                        Tile.TILE_SIZE * 0.5f, Tile.TILE_SIZE * 1.5f, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("debug weapon", "for swinging", Textures.atlasFindDrawable("item-weapon")),
                new ClickSwingComponent());
        result.moveTo(location);
        return result;
    }

    public static Entity printItemDots(Tile location) {
        Entity result = new Entity();
        result.addComponents(
                new ChunkDrawComponent(new TextureSet("panel-inventory"),
                        Tile.TILE_SIZE, Tile.TILE_SIZE, RenderLayer.objects),
                EntitySizeComponent.medium,
                new StorableComponent("put dots", "collision tester", Textures.atlasFindDrawable("panel-inventory")),
                new UseFunctionComponent((Entity user, Float x, Float y) -> {
                    Chunk chunk = user.getTile().chunk;
                    for (int i = -64; i <= 64; i += 2) {
                        for (int j = -64; j <= 64; j += 2) {
                            Collider collider = new RectangleCollider(1f);
                            collider.moveTo(x + i, y + j);
                            if (!chunk.collisionTree.anyElementsIntersect(collider)) {
                                CrossParticle p = Jade.renderer.particles.createParticle(CrossParticle.class,
                                        collider.getX(), collider.getY());
                                p.scale = 0.1f;
                            }

                        }
                    }
                    return true;
                })
        );
        result.moveTo(location);
        return result;
    }
}
