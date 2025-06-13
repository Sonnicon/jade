package sonnicon.jade.world.pathfinder;

import sonnicon.jade.entity.Traits;
import sonnicon.jade.util.Consumer2;
import sonnicon.jade.util.Pathfinder;
import sonnicon.jade.world.Tile;

import java.util.ArrayList;

public class TilePathfinder extends Pathfinder<Tile> {
    Tile destination = null;

    public ArrayList<Tile> findPath(Tile origin, Tile destination) {
        this.destination = destination;
        return findPath(origin);
    }

    @Override
    public void allAdjacent(Tile from, Consumer2<Tile, Float> cons) {
        from.allNearbyRound((Tile other, Byte dir) -> {
            if (!other.traits.hasTrait(Traits.Trait.blockMovement)) {
                boolean dirIsDiagonal = (dir & (dir - 1)) > 0;
                cons.apply(other, dirIsDiagonal ? 1.414f : 1f);
            }
        });
    }

    @Override
    public boolean isDestination(Tile point) {
        return point == destination;
    }

    @Override
    public float predicate(Tile source) {
        float dx = destination.getTileX() - source.getTileX();
        float dy = destination.getTileY() - source.getTileY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
