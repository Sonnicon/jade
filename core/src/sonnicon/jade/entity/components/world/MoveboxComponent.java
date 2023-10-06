package sonnicon.jade.entity.components.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Content;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.Direction;
import sonnicon.jade.util.Utils;
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

    private static final EventTypes.EntityMoveEvent MOVE_EVENT = MoveboxComponent::processMove;

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
        entity.events.register(MOVE_EVENT);

        coveredFind();
        coveredSwap();
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        entity.events.unregister(MOVE_EVENT);

        coveredTiles.forEach(t -> t.nearbyMoveboxes.remove(this));
    }

    public void coveredFind() {
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
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

        int posLeft = (x - size) / Tile.SUBTILE_NUM;
        int posRight = (x + size - 1) / Tile.SUBTILE_NUM;
        int posBottom = (y - size) / Tile.SUBTILE_NUM;
        int posTop = (y + size - 1) / Tile.SUBTILE_NUM;

        Tile tx = Content.world.getTile(posLeft, posBottom);
        while (tx != null && tx.getX() <= posRight) {
            Tile ty = tx;
            while (ty != null && ty.getY() <= posTop) {
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
                .filter(other -> Utils.overlapsSquare(operX, operY, operSize,
                        other.entity.getComponent(PositionComponent.class).getJointX(),
                        other.entity.getComponent(PositionComponent.class).getJointY(), other.size));
    }

    public boolean overlapsMovebox(MoveboxComponent other) {
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        PositionComponent oth = other.entity.getComponent(PositionComponent.class);
        return ((pos.getJointX() + size > oth.getJointX() - other.size) ^
                (pos.getJointX() - size > oth.getJointX() + other.size)) &&
                (pos.getJointY() + size > oth.getJointY() - other.size) ^
                        (pos.getJointY() - size > oth.getJointY() + other.size);
    }

    protected static void processMove(Entity entity) {
        MoveboxComponent moveboxComponent = entity.getComponent(MoveboxComponent.class);
        moveboxComponent.coveredFind();
        moveboxComponent.coveredSwap();
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(PositionComponent.class);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "size", size, "tiles", coveredTiles,
                "s_operX", operX, "s_operY", operY, "s_operSize", operSize, "s_coveredTilesOperation", coveredTilesOperation);
    }

    @Override
    public Component copy() {
        return ((MoveboxComponent) super.copy()).setup(size);
    }
}
