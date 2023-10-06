package sonnicon.jade.entity.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import sonnicon.jade.Jade;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.entity.components.world.SubtilePositionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.CharacterMoveAction;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.graphics.particles.TextParticle;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.input.WorldInput;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.HashSet;
import java.util.Iterator;

//todo remove debug component
public class KeyboardMovementComponent extends Component implements Clock.IUpdate {
    protected StorageComponent storageComponent;

    private final CharacterMoveAction characterMoveAction = (CharacterMoveAction) Actions.obtain(CharacterMoveAction.class).keepRef();

    private boolean pPressed = false, spacePressed = false;

    private static final Vector3 TEMP_VEC = new Vector3();

    private static final EventTypes.EntityMoveTileEvent onMoveTile = (Entity ent, Tile source, Tile destination) -> {
        if (destination != null) {
            Iterator<Entity> iter = destination.entities.iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (e.traits.hasTrait(Traits.Trait.stopPickup)) continue;

                if (ent.getComponent(StorageComponent.class).storage.addEntity(e)) {
                    if (e.getComponent(PositionComponent.class) != null) {
                        iter.remove();
                        PositionComponent epc = e.getComponent(PositionComponent.class);
                        WorldInput.readWorldPosition(TEMP_VEC, epc);
                        Jade.renderer.particles.createParticle(TextParticle.class, TEMP_VEC.x, TEMP_VEC.y).setText("item!");
                        epc.moveToNull();
                    }
                }
            }
        }
    };

    private static final EventTypes.EntityMoveEvent onMove = (e) -> {
        ((CachedDrawBatch) Renderer.Batch.terrainDynamic.batch).invalidate();
        ((CachedDrawBatch) Renderer.Batch.fow.batch).invalidate();
        Jade.renderer.viewOverlay.moveTo(e.getComponent(PositionComponent.class));
    };

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Clock.register(this);
        storageComponent = entity.getComponent(StorageComponent.class);

        entity.events.register(onMoveTile, onMove);
        Jade.renderer.viewOverlay.moveTo(entity.getComponent(PositionComponent.class));
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Clock.unregister(this);

        entity.events.unregister(onMoveTile, onMove);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(PositionComponent.class, StorageComponent.class);
    }

    @Override
    public void update(float delta) {
        //todo make this not poll
        byte moveDirection = 0;
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
        if (moveDirection != 0) {
            characterMoveAction.set((SubtilePositionComponent) entity.getComponent(PositionComponent.class), Direction.directionX(moveDirection), Direction.directionY(moveDirection)).time(1f).enqueue();
        }

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
