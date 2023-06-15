package sonnicon.jade.entity.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.Update;
import sonnicon.jade.world.Tile;

import java.util.Collections;
import java.util.HashSet;

public class KeyboardMovementComponent extends Component implements Update.IUpdate {
    protected PositionComponent positionComponent;

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Update.register(this);
        positionComponent = (PositionComponent) entity.components.get(PositionComponent.class);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Update.unregister(this);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return new HashSet<>(Collections.singletonList(PositionComponent.class));
    }

    @Override
    public Component copy() {
        return new KeyboardMovementComponent();
    }

    @Override
    public boolean compare(Component other) {
        return false;
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
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            destination = destination.getNearbyWest();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            destination = destination.getNearbySouth();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            destination = destination.getNearbyEast();
        }

        positionComponent.moveToTile(destination);

    }
}
