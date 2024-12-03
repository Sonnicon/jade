package sonnicon.jade.entity.components.world;

import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Translation;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class SubtilePositionComponent extends TilePositionComponent {
    protected byte subx, suby;

    public SubtilePositionComponent() {

    }

    public SubtilePositionComponent(Tile tile) {
        this(tile, (byte) (Tile.SUBTILE_NUM / 2), (byte) (Tile.SUBTILE_NUM / 2));
    }

    public SubtilePositionComponent(Tile tile, byte subx, byte suby) {
        super(tile);
        setup(subx, suby);
    }

    protected SubtilePositionComponent setup(byte x, byte y) {
        this.subx = x;
        this.suby = y;
        return this;
    }

    @Override
    public float getFloatingX() {
        return super.getFloatingX() + (subx - (Tile.SUBTILE_NUM >> 1)) * Tile.SUBTILE_DELTA;
    }

    @Override
    public float getFloatingY() {
        return super.getFloatingY() + (suby - (Tile.SUBTILE_NUM >> 1)) * Tile.SUBTILE_DELTA;
    }

    @Override
    public int getSubTileX() {
        return super.getSubTileX() + subx;
    }

    @Override
    public int getSubTileY() {
        return super.getSubTileY() + suby;
    }

    public byte getSubX() {
        return subx;
    }

    public byte getSubY() {
        return suby;
    }

    @Override
    public SubtilePositionComponent copy() {
        return ((SubtilePositionComponent) super.copy()).setup(subx, suby);
    }

    @Override
    public void moveToOther(PositionComponent other) {
        byte sx = (byte) (other.getSubTileX() % Tile.SUBTILE_NUM);
        byte sy = (byte) (other.getSubTileY() % Tile.SUBTILE_NUM);
        if (other instanceof TilePositionComponent) {
            moveTo(((TilePositionComponent) other).tile, sx, sy);
        } else {
            moveTo(Content.world.getTile(other.getTileX(), other.getTileY()), sx, sy);
        }
    }

    @Override
    public void moveToOther(PositionComponent other, Translation translation) {
        translation.apply(other);
        byte sx = (byte) ((translation.getResX() / Tile.SUBTILE_DELTA) % Tile.SUBTILE_NUM);
        byte sy = (byte) ((translation.getResY() / Tile.SUBTILE_DELTA) % Tile.SUBTILE_NUM);
        moveTo(Content.world.getTile((int) (translation.getResX() / Tile.TILE_SIZE),
                (int) (translation.getResY() / Tile.TILE_SIZE)), sx, sy);
    }

    public boolean tryMoveTo(byte subx, byte suby) {
        return canMoveTo(subx, suby) && moveTo(subx, suby);
    }

    public boolean moveTo(byte subx, byte suby) {
        moveToInternal(subx, suby);
        EventTypes.EntityMoveEvent.handle(entity.events, entity);
        return true;
    }

    private boolean moveToInternal(byte subx, byte suby) {
        this.subx = subx;
        this.suby = suby;
        return true;
    }

    public boolean tryMoveTo(Tile destination, byte subx, byte suby) {
        return canMoveTo(destination, subx, suby) && moveTo(destination, subx, suby);
    }

    public boolean moveTo(Tile destination, byte subx, byte suby) {
        Tile start = tile;
        boolean a = moveToInternal(subx, suby);
        boolean b = moveToInternal(destination);

        if (a) EventTypes.EntityMoveEvent.handle(entity.events, entity);
        if (b) fireTileEvent(entity, start, tile);
        return true;
    }

    public boolean tryMoveBy(byte subx, byte suby) {
        return moveByPos(subx, suby, false);
    }

    public boolean moveBy(byte subx, byte suby) {
        return moveByPos(subx, suby, true);
    }

    public boolean canMoveTo(byte subx, byte suby) {
        return canMoveTo(tile, subx, suby);
    }

    private boolean moveByPos(byte x, byte y, boolean force) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (byte) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (byte) Math.floorMod(y, Tile.SUBTILE_NUM);

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

    public boolean canMoveByPos(byte x, byte y) {
        x += subx;
        int tx = x / Tile.SUBTILE_NUM - (x < 0 ? 1 : 0);
        x = (byte) Math.floorMod(x, Tile.SUBTILE_NUM);

        y += suby;
        int ty = y / Tile.SUBTILE_NUM - (y < 0 ? 1 : 0);
        y = (byte) Math.floorMod(y, Tile.SUBTILE_NUM);

        Tile destTile = tile.chunk.world.getTile((tile.getX() + tx), (tile.getY() + ty));
        return destTile != null && canMoveTo(destTile, x, y);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "subx", subx, "suby", suby);
    }
}
