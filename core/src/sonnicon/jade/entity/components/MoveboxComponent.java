package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.JMath;
import sonnicon.jade.util.Structs;
import sonnicon.jade.world.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

public class MoveboxComponent extends Component {
    public short size;
    public ArrayList<Tile> coveredTiles;

    public static ArrayList<Tile> coveredTilesOperation = new ArrayList<>();
    private static int operX, operY;
    private static short operSize;

    private PositionComponent positionComponent;

    private static final EventTypes.EntityMovePosEvent MOVE_POS_EVENT = (Entity e, Short i1, Short i2, Short i3, Short i4) -> {
        processMove(e);
    };

    private static final EventTypes.EntityMoveTileEvent MOVE_TILE_EVENT = (Entity e, Tile i1, Tile i2) -> {
        processMove(e);
    };

    public MoveboxComponent() {

    }

    public MoveboxComponent(short size) {
        setup(size);
    }

    private MoveboxComponent setup(short size) {
        this.size = size;
        if (size > 0) {
            coveredTiles = new ArrayList<>();
        }
        return this;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        positionComponent = entity.getComponent(PositionComponent.class);
        entity.events.register(MOVE_POS_EVENT, MOVE_TILE_EVENT);

        coveredFind();
        coveredSwap();
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        positionComponent = null;
        entity.events.unregister(MOVE_POS_EVENT, MOVE_POS_EVENT);

        coveredTiles.forEach(t -> t.nearbyMoveboxes.remove(this));
    }

    public void coveredFind() {
        if (positionComponent != null) {
            coveredFind(positionComponent.getJointX(), positionComponent.getJointY(), size);
        } else {
            coveredTilesOperation.clear();
        }
    }

    public static void coveredFind(int x, int y, short size) {
        operX = x;
        operY = y;
        operSize = size;

        coveredTilesOperation.clear();

        short posLeft = (short) ((x - size) / Tile.SUBTILE_NUM);
        short posRight = (short) ((x + size - 1) / Tile.SUBTILE_NUM);
        short posBottom = (short) ((y - size) / Tile.SUBTILE_NUM);
        short posTop = (short) ((y + size - 1) / Tile.SUBTILE_NUM);

        Tile tx = Content.world.getTile(posLeft, posBottom);
        while (tx.getX() <= posRight) {
            Tile ty = tx;
            while (ty.getY() <= posTop) {
                coveredTilesOperation.add(ty);
                ty = ty.getNearby(Direction.NORTH);
            }
            tx = tx.getNearby(Direction.EAST);
        }
    }

    public void coveredSwap() {
        coveredTiles.forEach(t -> t.nearbyMoveboxes.remove(this));
        coveredTilesOperation.forEach(t -> t.nearbyMoveboxes.add(this));

        ArrayList<Tile> temp = coveredTiles;
        coveredTiles = coveredTilesOperation;
        coveredTilesOperation = temp;
    }

    public static Stream<MoveboxComponent> coveredStream() {
        return coveredTilesOperation.stream()
                .flatMap(tile -> tile.nearbyMoveboxes.stream())
                .filter(other -> JMath.overlapsSquare(operX, operY, operSize,
                        other.positionComponent.getJointX(), other.positionComponent.getJointY(), other.size));
    }

    public boolean overlapsMovebox(MoveboxComponent other) {
        return ((positionComponent.getJointX() + size > other.positionComponent.getJointX() - other.size) ^
                (positionComponent.getJointX() - size > other.positionComponent.getJointX() + other.size)) &&
                (positionComponent.getJointY() + size > other.positionComponent.getJointY() - other.size) ^
                        (positionComponent.getJointY() - size > other.positionComponent.getJointY() + other.size);
    }

    protected static void processMove(Entity entity) {
        MoveboxComponent moveboxComponent = entity.getComponent(MoveboxComponent.class);
        moveboxComponent.coveredFind();
        moveboxComponent.coveredSwap();
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Structs.setFrom(PositionComponent.class);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Structs.mapExtendFrom(super.debugProperties(), "size", size, "tiles", coveredTiles);
    }

    @Override
    public Component copy() {
        return ((MoveboxComponent) super.copy()).setup(size);
    }
}
