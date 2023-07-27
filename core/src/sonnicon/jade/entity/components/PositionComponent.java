package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.world.Tile;

public class PositionComponent extends Component {
    public Tile tile;

    public PositionComponent() {

    }

    public PositionComponent(Tile tile) {
        setup(tile);
    }

    protected PositionComponent setup(Tile tile) {
        this.tile = tile;
        return this;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        moveToTile(tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        // move out of tile so that we don't end up with stray references
        moveToTile(null);
    }

    @Override
    public boolean compare(Component other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return true;
    }

    @Override
    public PositionComponent copy() {
        return ((PositionComponent) super.copy()).setup(tile);
    }

    public void moveToTile(Tile destination) {
        if (entity == null) {
            return;
        }

        Tile source = tile;
        tile = destination;

        if (source != null) {
            source.entities.remove(entity);
        }
        if (destination != null) {
            destination.entities.add(entity);
        }

        // Ordering
        if (source != null) {
            source.events.handle(EntityMoveEvent.class, entity, source, destination);
        }
        if (destination != null) {
            destination.events.handle(EntityMoveEvent.class, entity, source, destination);
        }
        entity.events.handle(EntityMoveEvent.class, entity, source, destination);
    }

    public static final class EntityMoveEvent {
    }
}
