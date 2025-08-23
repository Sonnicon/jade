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
import sonnicon.jade.game.actions.CollisionMoveAction;
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

    private byte lastDir = Directions.ALL;

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
            storageComponent.storage.addEntity(ItemPrinter.printItemDots(null));

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (!spacePressed) {
                spacePressed = true;
                Clock.tick(0.1f);
            }
        } else {
            spacePressed = false;
        }
        Clock.tick(100f);

        //todo replace this wall slide code
        byte reqDir = Directions.ALL;
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT)) reqDir &= ~Directions.WESTWARD;
        if (!Gdx.input.isKeyPressed(Input.Keys.RIGHT)) reqDir &= ~Directions.EASTWARD;
        if (!Gdx.input.isKeyPressed(Input.Keys.DOWN)) reqDir &= ~Directions.SOUTHWARD;
        if (!Gdx.input.isKeyPressed(Input.Keys.UP)) reqDir &= ~Directions.NORTHWARD;


        if (reqDir != lastDir) {
            lastDir = reqDir;

            Actions.actionsList.stream()
                    .filter(a -> a instanceof CollisionMoveAction)
                    .forEach(p -> p.interrupt(Clock.getTickNum()));

            if (!Directions.is(reqDir, Directions.NONE)) {
                CollisionMoveAction cma = ObjectPool.obtain(CollisionMoveAction.class);
                cma.set(entity, Directions.directionX(reqDir) * 24f, Directions.directionY(reqDir) * 24f, 0f);
                cma.setDuration(0.6f);
                cma.then(cma);
                cma.start();
            }
        }


/*
        if (reqDir == Directions.ALL) {
            if (lastDir != Directions.NONE) {
                Actions.actionsList.stream()
                        .filter(a -> a instanceof CollisionMoveAction)
                        .forEach(p -> p.interrupt(Clock.getTickNum()));
                lastDir = Directions.NONE;
            }
        } else if (lastDir != reqDir) {
            Collider collider = entity.getComponent(CollisionComponent.class).collider;

            byte reqDirX = Directions.directionX(reqDir);
            if (Collisions.collisionAt(collider, 4f * reqDirX, 0f, 0f)) {
                reqDir &= ~Directions.HORIZONTALWARD;
            }

            byte reqDirY = Directions.directionY(reqDir);
            if (Collisions.collisionAt(collider, 0f, 4f * reqDirY, 0f)) {
                reqDir &= ~Directions.VERTICALWARD;
            }

            if (Directions.is(reqDir, Directions.CARDINAL)) {
                reqDir &= Directions.CARDINAL;
            }

            if (lastDir != reqDir) {
                // We are actually doing something
                lastDir = reqDir;
                Actions.actionsList.stream()
                        .filter(a -> a instanceof CollisionMoveAction)
                        .forEach(p -> p.interrupt(Clock.getTickNum()));

                if (Directions.is(reqDir, Directions.HORIZONTALWARD)) {

                    CollisionMoveAction cma = ObjectPool.obtain(CollisionMoveAction.class);
                    cma.set(entity, Directions.directionX(reqDir) * 24f, 0f, 0f);
                    cma.setDuration(0.6f);
                    cma.then(cma);
                    cma.start();
                }

                if (Directions.is(reqDir, Directions.VERTICALWARD)) {
                    CollisionMoveAction cma = ObjectPool.obtain(CollisionMoveAction.class);
                    cma.set(entity, 0f, Directions.directionY(reqDir) * 24f, 0f);
                    cma.setDuration(0.6f);
                    cma.then(cma);
                    cma.start();

                }
            }
        }
*/

    }

    @Override
    public boolean compare(IComparable other) {
        return false;
    }
}
