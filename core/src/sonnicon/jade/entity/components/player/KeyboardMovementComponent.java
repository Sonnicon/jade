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
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.draw.CachedDrawBatch;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

import java.util.HashSet;

//todo remove debug component
public class KeyboardMovementComponent extends Component implements Clock.IOnFrame {
    protected StorageComponent storageComponent;

    private boolean pPressed = false;
    private boolean spacePressed = false;

    private static final EventTypes.EntityMoveEvent onMove = (e) -> {
        ((CachedDrawBatch) RenderLayer.terrainTop.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.terrainSides.batch).invalidate();
        ((CachedDrawBatch) RenderLayer.fow.batch).invalidate();
        Content.viewOverlay.forceMoveTo(e);
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
        Content.viewOverlay.forceMoveTo(entity);
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

        //todo remove this
        float moveX = 0f;
        float moveY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1f;
        }
        if (moveX != 0f || moveY != 0f) {
            //todo sliding along edges
            float moved = entity.moveBy(moveX, moveY);
            if (moved < 0.98f && moveX != 0f && moveY != 0f) {
                entity.moveBy(moveX * (1f - moved), 0);
                entity.moveBy(0, moveY * (1f - moved));
            }
        }
    }

    @Override
    public boolean compare(IComparable other) {
        return false;
    }
}
