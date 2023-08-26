package sonnicon.jade.entity.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.particles.TextParticle;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Structs;
import sonnicon.jade.world.Tile;

import java.util.HashSet;
import java.util.Iterator;

//todo remove debug component
public class KeyboardMovementComponent extends Component implements Clock.ITicking, Clock.IUpdate {
    protected PositionComponent positionComponent;
    protected StorageComponent storageComponent;

    private boolean pPressed = false, spacePressed = false;
    private byte moveDirection = 0;

    private static final Vector3 TEMP_VEC = new Vector3();

    private static final EventTypes.EntityMoveEvent onMove = (i1, i2, i3) -> {
        ((CachedDrawBatch) Renderer.Batch.dynamicTerrain.batch).invalidate();
        ((CachedDrawBatch) Renderer.Batch.fow.batch).invalidate();
    };

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Clock.register(this);
        positionComponent = entity.getComponent(PositionComponent.class);
        storageComponent = entity.getComponent(StorageComponent.class);

        entity.events.register(onMove);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Clock.unregister(this);

        entity.events.unregister(onMove);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Structs.setFrom(PositionComponent.class, StorageComponent.class);
    }

    @Override
    public void tick(float delta) {
        Tile destination = positionComponent.tile;
        if (destination == null) {
            return;
        }

        destination = destination.getNearby(moveDirection);
        moveDirection = 0;

        if (destination != null && destination != positionComponent.tile) {
            Iterator<Entity> iter = destination.entities.iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (storageComponent.storage.addEntity(e)) {
                    if (e.getComponent(PositionComponent.class) != null) {
                        iter.remove();
                        PositionComponent epc = e.getComponent(PositionComponent.class);
                        Content.world.getTileScreenPosition(TEMP_VEC, epc.tile);
                        Jade.renderer.particles.createParticle(TextParticle.class, TEMP_VEC.x, TEMP_VEC.y).setText("item!");
                        epc.moveToTile(null);
                    }
                }
            }
            positionComponent.moveToTile(destination);
        }
    }

    @Override
    public void update(float delta) {
        //todo make this not poll
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveDirection |= Direction.NORTH;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveDirection |= Direction.WEST;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveDirection |= Direction.SOUTH;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveDirection |= Direction.EAST;
        }
        moveDirection = Direction.flatten(moveDirection);

        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            ((StageIngame) Gamestate.State.ingame.getStage()).panelInventory.show(storageComponent.storage);
        }

        if (pPressed && !Gdx.input.isKeyPressed(Input.Keys.P)) {
            pPressed = false;
        } else if (!pPressed && Gdx.input.isKeyPressed(Input.Keys.P)) {
            pPressed = true;
            storageComponent.storage.addEntity(ItemPrinter.printItemDebug(null));
        }

        if (spacePressed && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            spacePressed = false;
        } else if (!spacePressed && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            spacePressed = true;
            Clock.tick(1f);
        }
    }

    @Override
    public boolean compare(IComparable other) {
        return false;
    }
}
