package sonnicon.jade.entity.components.world;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

@EventGenerator(id = "EntityMoveTile", param = {Entity.class, Tile.class, Tile.class}, label = {"entity", "source", "destination"})
@EventGenerator(id = "EntityMovePos", param = {Entity.class, Short.class, Short.class, Short.class, Short.class}, label = {"entity", "sourceX", "sourceY", "destX", "destY"})
public class PositionComponent extends Component {
    public Tile tile;
    public short subx, suby;

    public PositionComponent() {

    }

    public PositionComponent(Tile tile) {
        setup(tile, (short) (Tile.SUBTILE_NUM / 2), (short) (Tile.SUBTILE_NUM / 2));
    }

    public PositionComponent(Tile tile, short x, short y) {
        setup(tile, x, y);
    }

    protected PositionComponent setup(Tile tile, short x, short y) {
        this.tile = tile;
        this.subx = x;
        this.suby = y;
        return this;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        moveTo(tile);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        // move out of tile so that we don't end up with stray references
        moveTo(null);
    }

    @Override
    public boolean compare(IComparable other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return super.compare(other);
    }

    @Override
    public PositionComponent copy() {
        return ((PositionComponent) super.copy()).setup(tile, subx, suby);
    }

    public void moveTo(Tile destination) {
        moveTo(destination, subx, suby);
    }

    public boolean moveTo(Tile destination, short newx, short newy) {
        Tile source = tile;
        tile = destination;

        if (source != null) {
            source.entities.remove(entity);
        }
        if (destination != null) {
            destination.entities.add(entity);
        }

        short oldx = this.subx, oldy = this.suby;
        this.subx = newx;
        this.suby = newy;

        // Ordering
        if (source != null) {
            EventTypes.EntityMoveTileEvent.handle(source.events, entity, source, destination);
        }
        if (destination != null) {
            EventTypes.EntityMoveTileEvent.handle(destination.events, entity, source, destination);
        }
        EventTypes.EntityMoveTileEvent.handle(entity.events, entity, source, destination);
        EventTypes.EntityMovePosEvent.handle(entity.events, entity, oldx, oldy, subx, suby);
        return true;
    }

    public void moveByPos(short x, short y) {
        moveByPos(x, y, true);
    }

    public boolean tryMoveByPos(short x, short y) {
        return moveByPos(x, y, false);
    }

    private boolean moveByPos(short x, short y, boolean force) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (short) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (short) Math.floorMod(y, Tile.SUBTILE_NUM);

        Tile destTile = tile.chunk.world.getTile((short) (tile.getX() + tx), (short) (tile.getY() + ty));

        if (force || canMoveTo(destTile, x, y)) {
            moveTo(destTile, x, y);
            return true;
        }
        return false;
    }

    public boolean canMoveTo(Tile destination, short nx, short ny) {
        if (entity == null) {
            return true;
        }

        MoveboxComponent moveboxComponent = entity.getComponent(MoveboxComponent.class);

        if (moveboxComponent == null || entity.traits.hasTrait(Traits.Trait.incorporeal)) {
            return true;
        }

        MoveboxComponent.coveredFind(destination.getJointX() + nx, destination.getJointY() + ny, moveboxComponent.size);

        if (MoveboxComponent.coveredTilesOperation.stream().anyMatch(t -> t.traits.hasTrait(Traits.Trait.blockMovement))) {
            return false;
        }

        return MoveboxComponent.coveredStream().noneMatch(
                o -> o != moveboxComponent && o.entity.traits.hasTrait(Traits.Trait.blockMovement));
    }

    public float getDrawX() {
        return tile.getDrawX() + Tile.SUBTILE_DELTA * subx;
    }

    public float getDrawY() {
        return tile.getDrawY() + Tile.SUBTILE_DELTA * suby;
    }

    public int getJointX() {
        return tile.getJointX() + subx;
    }

    public int getJointY() {
        return tile.getJointY() + suby;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "tile", tile, "subx", subx, "suby", suby, "jointx", getJointX(), "jointy", getJointY());
    }
}
