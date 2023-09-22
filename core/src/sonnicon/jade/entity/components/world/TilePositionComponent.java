package sonnicon.jade.entity.components.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class TilePositionComponent extends PositionComponent {
    public Tile tile;
    public float rotation = 0f;

    public TilePositionComponent() {

    }

    public TilePositionComponent(Tile tile) {
        setup(tile);
    }

    protected TilePositionComponent setup(Tile tile) {
        this.tile = tile;
        return this;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        moveTo(tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        // move out of tile so that we don't end up with stray references
        moveToNull();
        super.removeFromEntity(entity);
    }

    @Override
    public TilePositionComponent copy() {
        return ((TilePositionComponent) super.copy()).setup(tile);
    }

    public boolean tryMoveTo(Tile destination) {
        return canMoveTo(destination) && moveTo(destination);
    }

    public boolean moveTo(Tile destination) {
        Tile source = tile;
        if (moveToInternal(destination)) {
            fireTileEvent(source, tile);
            EventTypes.EntityMoveEvent.handle(entity.events, entity);
            return true;
        } else {
            return false;
        }
    }

    protected boolean moveToInternal(Tile destination) {
        if (tile == destination && destination.entities.contains(entity)) {
            return false;
        }

        Tile source = tile;
        tile = destination;

        if (source != null) {
            source.entities.remove(entity);
        }
        if (destination != null) {
            destination.entities.add(entity);
        }
        return true;
    }

    public boolean canMoveTo(Tile destination) {
        return canMoveTo(destination, (short) 0, (short) 0);
    }

    protected boolean canMoveTo(Tile destination, short subx, short suby) {
        if (entity == null) {
            return true;
        }

        MoveboxComponent moveboxComponent = entity.getComponent(MoveboxComponent.class);

        if (moveboxComponent == null || entity.traits.hasTrait(Traits.Trait.incorporeal)) {
            return true;
        }

        MoveboxComponent.coveredFind(destination.getJointX() + subx, destination.getJointY() + suby, moveboxComponent.size);

        if (MoveboxComponent.coveredTilesOperation.stream().anyMatch(t -> t.traits.hasTrait(Traits.Trait.blockMovement))) {
            return false;
        }

        return MoveboxComponent.coveredStream().noneMatch(
                o -> o != moveboxComponent && o.entity.traits.hasTrait(Traits.Trait.blockMovement));
    }

    @Override
    public void moveToOther(PositionComponent other) {
        if (other instanceof TilePositionComponent) {
            moveTo(((TilePositionComponent) other).tile);
        } else {
            moveTo(Content.world.getTile(other.getTileX(), other.getTileY()));
        }
    }

    @Override
    public void moveToNull() {
        moveTo(null);
    }

    @Override
    public boolean isInNull() {
        return tile == null;
    }

    protected void fireTileEvent(Tile source, Tile destination) {
        if (source != null) {
            EventTypes.EntityMoveTileEvent.handle(source.events, entity, source, destination);
        }
        if (destination != null) {
            EventTypes.EntityMoveTileEvent.handle(destination.events, entity, source, destination);
        }
        EventTypes.EntityMoveTileEvent.handle(entity.events, entity, source, destination);
    }

    @Override
    public float getDrawX() {
        return tile.getDrawX();
    }

    @Override
    public float getDrawY() {
        return tile.getDrawY();
    }

    @Override
    public int getJointX() {
        return tile.getJointX();
    }

    @Override
    public int getJointY() {
        return tile.getJointY();
    }

    @Override
    public int getTileX() {
        return tile.getX();
    }

    @Override
    public int getTileY() {
        return tile.getY();
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public Tile getTile() {
        return tile;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "tile", tile);
    }
}
