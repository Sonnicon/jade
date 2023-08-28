package sonnicon.jade.entity.components;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Structs;
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

    protected PositionComponent setup(Tile tile, short x, short y) {
        this.tile = tile;
        this.subx = x;
        this.suby = y;
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
    public boolean compare(IComparable other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return true;
    }

    @Override
    public PositionComponent copy() {
        return ((PositionComponent) super.copy()).setup(tile, subx, suby);
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
            EventTypes.EntityMoveTileEvent.handle(source.events, entity, source, destination);
        }
        if (destination != null) {
            EventTypes.EntityMoveTileEvent.handle(destination.events, entity, source, destination);
        }
        EventTypes.EntityMoveTileEvent.handle(entity.events, entity, source, destination);
    }

    public void moveToPos(short x, short y) {
        if (entity == null) {
            return;
        }

        EventTypes.EntityMovePosEvent.handle(entity.events, entity, this.subx, this.suby, this.subx = x, this.suby = y);
    }

    public void moveByPos(short x, short y) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (short) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (short) Math.floorMod(y, Tile.SUBTILE_NUM);

        moveToPos(x, y);
        if (tx != 0 || ty != 0) {
            moveToTile(tile.chunk.world.getTile((short) (tile.getX() + tx), (short) (tile.getY() + ty)));
        }
    }

    public float getDrawX() {
        return tile.getDrawX() + Tile.SUBTILE_DELTA * subx;
    }

    public float getDrawY() {
        return tile.getDrawY() + Tile.SUBTILE_DELTA * suby;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Structs.mapExtendFrom(super.debugProperties(), "tile", tile, "subx", subx, "suby", suby);
    }
}
