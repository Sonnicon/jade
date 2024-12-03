package sonnicon.jade.entity.components.world;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.Traits;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Translation;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

@EventGenerator(id = "EntityMoveTile", param = {Entity.class, Tile.class, Tile.class}, label = {"entity", "source", "destination"})
@EventGenerator(id = "EntityMove", param = {Entity.class}, label = {"entity"})
@EventGenerator(id = "EntityRotate", param = {Entity.class, Float.class}, label = {"entity", "newAngle"})
public abstract class PositionComponent extends Component {

    public PositionComponent() {

    }

    @Override
    public Class<? extends Component> getKeyClass() {
        return PositionComponent.class;
    }

    @Override
    public boolean compare(IComparable other) {
        // Checking if two entities are in the same place isn't really the goal of this function
        return super.compare(other);
    }

    public abstract void moveToOther(PositionComponent other);

    public abstract void moveToOther(PositionComponent other, Translation translation);

    public abstract void moveToNull();

    public abstract boolean isInNull();

    public abstract void rotate(float deltaAngle);

    public abstract void rotateTo(float newAngle);

    // Tile grid positions
    public abstract int getTileX();
    public abstract int getTileY();

    // Sub-tile grid positions
    public abstract int getSubTileX();
    public abstract int getSubTileY();

    // Floating world-space positions
    public abstract float getFloatingX();
    public abstract float getFloatingY();

    // Floating screen-space positions
    public float getDrawX() {
        if (isInNull()) return Float.NaN;
        return getFloatingX() + AnimationComponent.getNestedX(entity);
    }

    public float getDrawY() {
        if (isInNull()) return Float.NaN;
        return getFloatingY() + AnimationComponent.getNestedY(entity);
    }

    public abstract float getRotation();

    public abstract Tile getTile();

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "getFloatingX", getFloatingX(), "getFloatingY", getFloatingY(),
                "getDrawX", getDrawX(), "getDrawY", getDrawY(),
                "getSubTileX", getSubTileX(), "getSubTileY", getSubTileY(),
                "getTileX", getTileX(), "getTileY", getTileY(),
                "getRotation", getRotation());
    }

    public static boolean canMoveTo(Entity entity, Tile destination, short subx, short suby) {
        if (entity == null) {
            return true;
        }

        MoveboxComponent moveboxComponent = entity.getComponent(MoveboxComponent.class);

        if (moveboxComponent == null || entity.traits.hasTrait(Traits.Trait.incorporeal)) {
            return true;
        }

        MoveboxComponent.coveredFind(destination.getSubTileX() + subx, destination.getSubTileY() + suby, moveboxComponent.size);

        if (MoveboxComponent.coveredTilesOperation.stream().anyMatch(t -> t.traits.hasTrait(Traits.Trait.blockMovement))) {
            return false;
        }

        return MoveboxComponent.coveredStream().noneMatch(
                o -> o != moveboxComponent && o.entity.traits.hasTrait(Traits.Trait.blockMovement));
    }


    //helper
    protected static void fireTileEvent(Entity entity, Tile source, Tile destination) {
        if (source != null) {
            EventTypes.EntityMoveTileEvent.handle(source.events, entity, source, destination);
        }
        if (destination != null) {
            EventTypes.EntityMoveTileEvent.handle(destination.events, entity, source, destination);
        }
        EventTypes.EntityMoveTileEvent.handle(entity.events, entity, source, destination);
    }
}
