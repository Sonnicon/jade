package sonnicon.jade.world.pathfinder;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.Content;
import sonnicon.jade.util.Consumer2;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.Pathfinder;
import sonnicon.jade.util.Point;
import sonnicon.jade.world.Tile;

import java.util.ArrayList;

public class LocalPathfinder extends Pathfinder<Short> {
    protected Tile originTile;
    protected byte originPointX, originPointY;

    protected Tile destTile;
    protected byte destPointX, destPointY;

    protected Entity entity;
    protected boolean exact;

    public ArrayList<Short> findPath(Tile origin, short originPoint, Tile dest, Entity entity) {

        short destSub = Point.getPoint((byte) Tile.HALF_SUBTILE_NUM, (byte) Tile.HALF_SUBTILE_NUM);
        return findPath(origin, originPoint, dest, destSub, entity, false);
    }

    public ArrayList<Short> findPath(Tile from, short fromSub, Tile dest, short destSub, Entity entity, boolean exact) {
        this.originTile = from;
        this.originPointX = Point.getXFromPoint(fromSub);
        this.originPointY = Point.getYFromPoint(fromSub);

        this.destTile = dest;
        this.destPointX = (byte)
                (dest.getSubTileX() + Point.getXFromPoint(destSub) - from.getSubTileX() - originPointX);
        this.destPointY = (byte)
                (dest.getSubTileY() + Point.getYFromPoint(destSub) - from.getSubTileY() - originPointY);

        this.entity = entity;
        this.exact = exact;

        return findPath((short) 0);
    }

    @Override
    public void allAdjacent(Short from, Consumer2<Short, Float> cons) {
        Direction.round(d -> {
            short point = Point.addToPoint(from, Direction.directionX(d), Direction.directionY(d));
            int otherSubX = originTile.getSubTileX() + originPointX + Point.getXFromPoint(point);
            int otherSubY = originTile.getSubTileY() + originPointY + Point.getYFromPoint(point);

            Tile other = Content.world.getTileJoint(otherSubX, otherSubY);
            if (other == null) return;

            if (entity == null || !PositionComponent.canMoveTo(entity, other,
                    (short) (otherSubX % Tile.SUBTILE_NUM),
                    (short) (otherSubY % Tile.SUBTILE_NUM))) {
                return;
            }

            boolean dirIsDiagonal = (d & (d - 1)) > 0;
            cons.apply(point, dirIsDiagonal ? 1.414f : 1f);
        });
    }

    @Override
    protected boolean isDestination(Short point) {
        byte px = Point.getXFromPoint(point);
        byte py = Point.getYFromPoint(point);
        Tile pointTile = Content.world.getTileJoint(
                originTile.getSubTileX() + originPointX + px,
                originTile.getSubTileY() + originPointY + py
        );
        return (pointTile == destTile) && (!exact || (px == destPointX && py == destPointY));
    }

    @Override
    public float predicate(Short source) {
        float dx = Point.getXFromPoint(source) - destPointX;
        float dy = Point.getYFromPoint(source) - destPointY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
