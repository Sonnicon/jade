package sonnicon.jade.entity.components.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.game.actions.CollisionRelativeMoveAction;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.Directions;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.HashSet;

//todo remove all of this
public class KeyboardMovementComponent extends Component implements Clock.IOnFrame {
    protected StorageComponent storageComponent;

    private boolean pPressed = false;
    private boolean spacePressed = false;

    private byte lastMoveDirection = Directions.ALL;

    private static final EventTypes.EntityMoveEvent onMove = (e) -> {
        ((CachedDrawBatch) RenderLayer.terrainTop.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.terrainSides.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.fow.batch).invalidate();
        Content.viewOverlay.moveTo(e);
        Jade.renderer.camera.position.x = e.getX();
        Jade.renderer.camera.position.y = e.getY();
        Jade.renderer.updateCamera();
        Jade.renderer.events.handle(EventTypes.CameraMoveEvent.class, Jade.renderer.camera);
    };

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Clock.register(this);
        storageComponent = entity.getComponent(StorageComponent.class);

        entity.events.register(onMove);
        onMove.apply(entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Clock.unregister(this);

        entity.events.unregister(onMove);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(StorageComponent.class);
    }

    @Override
    public void onFrame(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            ((StageIngame) Gamestate.State.ingame.getStage()).panelInventory.show(storageComponent.storage);
        }

        if (pPressed && !Gdx.input.isKeyPressed(Input.Keys.P)) {
            pPressed = false;
        } else if (!pPressed && Gdx.input.isKeyPressed(Input.Keys.P)) {
            pPressed = true;
            storageComponent.storage.addEntity(ItemPrinter.printWeaponDebug(null));
            storageComponent.storage.addEntity(ItemPrinter.printItemRedbox(null));
            storageComponent.storage.addEntity(ItemPrinter.printItemDebug(null));

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!spacePressed) {
                spacePressed = true;
                Clock.tick(1f);
            }
        } else {
            spacePressed = false;
        }
        Clock.tick(100f);

        byte moveX = 0;
        byte moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1;

        byte direction = Directions.encodePrecise(moveX, moveY);
        if (lastMoveDirection != direction) {
            CollisionRelativeMoveAction prevAction = (CollisionRelativeMoveAction) Actions.actionsList.stream()
                    .filter(a -> a instanceof CollisionRelativeMoveAction)
                    .findFirst()
                    .orElse(null);

            if (prevAction != null) {
                prevAction.interrupt(Clock.getTickNum());
            }
            lastMoveDirection = direction;
            if (direction != Directions.NONE) {
                CollisionRelativeMoveAction cma = ObjectPool.obtain(CollisionRelativeMoveAction.class);
                cma.set(entity, Directions.directionX(direction) * 24f, Directions.directionY(direction) * 24f);
                cma.setDuration(1f);
                cma.then(cma);
                cma.start();
            }

        }

    }

    @Override
    public boolean compare(IComparable other) {
        return false;
    }
}
