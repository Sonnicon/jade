package sonnicon.jade.entity.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import sonnicon.jade.content.ItemPrinter;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.Update;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.Sets;
import sonnicon.jade.world.Tile;

import java.util.HashSet;
import java.util.Iterator;

//todo remove debug component
public class KeyboardMovementComponent extends Component implements Update.IUpdate {
    protected PositionComponent positionComponent;
    protected StorageComponent storageComponent;

    private boolean pPressed = false;

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Update.register(this);
        positionComponent = entity.getComponent(PositionComponent.class);
        storageComponent = entity.getComponent(StorageComponent.class);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Update.unregister(this);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Sets.from(PositionComponent.class, StorageComponent.class);
    }

    @Override
    public void update(float delta) {
        Tile destination = positionComponent.tile;
        if (destination == null) {
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            destination = destination.getNearbyNorth();
        }
        if (destination != null && Gdx.input.isKeyPressed(Input.Keys.A)) {
            destination = destination.getNearbyWest();
        }
        if (destination != null && Gdx.input.isKeyPressed(Input.Keys.S)) {
            destination = destination.getNearbySouth();
        }
        if (destination != null && Gdx.input.isKeyPressed(Input.Keys.D)) {
            destination = destination.getNearbyEast();
        }

        if (destination != null && destination != positionComponent.tile) {
            Iterator<Entity> iter = destination.entities.iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (storageComponent.storage.addEntity(e)) {
                    if (e.getComponent(PositionComponent.class) != null) {
                        iter.remove();
                        e.getComponent(PositionComponent.class).moveToTile(null);

                    }
                }
            }
            positionComponent.moveToTile(destination);
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
    }

    @Override
    public boolean compare(Component other) {
        return false;
    }
}
