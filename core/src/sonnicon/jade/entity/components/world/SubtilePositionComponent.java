package sonnicon.jade.entity.components.world;

import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Translation;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class SubtilePositionComponent extends TilePositionComponent {
    public short subx, suby;

    public SubtilePositionComponent() {

    }

    public SubtilePositionComponent(Tile tile) {
        this(tile, (short) (Tile.SUBTILE_NUM / 2), (short) (Tile.SUBTILE_NUM / 2));
    }

    public SubtilePositionComponent(Tile tile, short x, short y) {
        super(tile);
        setup(x, y);
    }

    protected SubtilePositionComponent setup(short x, short y) {
        this.subx = x;
        this.suby = y;
        return this;
    }

    @Override
    public float getDrawX() {
        return super.getDrawX() + (subx - (Tile.SUBTILE_NUM >> 1)) * Tile.SUBTILE_DELTA;
    }

    @Override
    public float getDrawY() {
        return super.getDrawY() + (suby - (Tile.SUBTILE_NUM >> 1)) * Tile.SUBTILE_DELTA;
    }

    @Override
    public int getJointX() {
        return super.getJointX() + subx;
    }

    @Override
    public int getJointY() {
        return super.getJointY() + suby;
    }

    @Override
    public SubtilePositionComponent copy() {
        return ((SubtilePositionComponent) super.copy()).setup(subx, suby);
    }

    @Override
    public void moveToOther(PositionComponent other) {
        short sx = (short) (other.getJointX() % Tile.SUBTILE_NUM);
        short sy = (short) (other.getJointY() % Tile.SUBTILE_NUM);
        if (other instanceof TilePositionComponent) {
            moveTo(((TilePositionComponent) other).tile, sx, sy);
        } else {
            moveTo(Content.world.getTile(other.getTileX(), other.getTileY()), sx, sy);
        }
    }

    @Override
    public void moveToOther(PositionComponent other, Translation translation) {
        translation.apply(other);
        short sx = (short) ((translation.getResX() / Tile.SUBTILE_DELTA) % Tile.SUBTILE_NUM);
        short sy = (short) ((translation.getResY() / Tile.SUBTILE_DELTA) % Tile.SUBTILE_NUM);
        moveTo(Content.world.getTile((int) (translation.getResX() / Tile.TILE_SIZE), (int) (translation.getResY() / Tile.TILE_SIZE)), sx, sy);
    }

    public boolean tryMoveTo(short subx, short suby) {
        return canMoveTo(subx, suby) && moveTo(subx, suby);
    }

    public boolean moveTo(short subx, short suby) {
        moveToInternal(subx, suby);
        EventTypes.EntityMoveEvent.handle(entity.events, entity);
        return true;
    }

    private boolean moveToInternal(short subx, short suby) {
        this.subx = subx;
        this.suby = suby;
        return true;
    }

    public boolean tryMoveTo(Tile destination, short subx, short suby) {
        return canMoveTo(destination, subx, suby) && moveTo(destination, subx, suby);
    }

    public boolean moveTo(Tile destination, short subx, short suby) {
        Tile start = tile;
        boolean a = moveToInternal(subx, suby);
        boolean b = moveToInternal(destination);

        if (a) EventTypes.EntityMoveEvent.handle(entity.events, entity);
        if (b) fireTileEvent(entity, start, tile);
        return true;
    }

    public boolean tryMoveBy(short subx, short suby) {
        return moveByPos(subx, suby, false);
    }

    public boolean moveBy(short subx, short suby) {
        return moveByPos(subx, suby, true);
    }

    public boolean canMoveTo(short subx, short suby) {
        return canMoveTo(tile, subx, suby);
    }

    private boolean moveByPos(short x, short y, boolean force) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (short) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (short) Math.floorMod(y, Tile.SUBTILE_NUM);

        Tile destTile = tile.chunk.world.getTile((tile.getX() + tx), (tile.getY() + ty));
        if (destTile == null) {
            return false;
        }

        if (force || canMoveTo(destTile, x, y)) {
            moveTo(destTile, x, y);
            return true;
        }
        return false;
    }

    public boolean canMoveByPos(short x, short y) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (short) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (short) Math.floorMod(y, Tile.SUBTILE_NUM);

        Tile destTile = tile.chunk.world.getTile((tile.getX() + tx), (tile.getY() + ty));
        return destTile != null && canMoveTo(destTile, x, y);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "subx", subx, "suby", suby);
    }
}
