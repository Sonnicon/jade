package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.world.Tile;

public class PositionComponent extends Component {
    public Tile tile;

    public PositionComponent(Tile tile) {
        this.tile = tile;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        // we can add the entity now
        moveToTile(tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        // move out of tile so that we don't end up with stray references
        moveToTile(null);
    }

    @Override
    public Component copy() {
        return new PositionComponent(tile);
    }

    @Override
    public boolean compare(Component other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return true;
    }

    public void moveToTile(Tile destination) {
        if (tile != null) {
            tile.entities.remove(entity);
        }
        tile = destination;
        if (destination != null) {
            destination.entities.add(entity);
        }
    }
}
