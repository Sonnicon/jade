package sonnicon.jade.entity.components.world;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
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

    public abstract float getDrawX();

    public abstract float getDrawY();

    public abstract int getJointX();

    public abstract int getJointY();

    public abstract int getTileX();

    public abstract int getTileY();

    public abstract float getRotation();

    public abstract Tile getTile();

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(),
                "getDrawX", getDrawX(), "getDrawY", getDrawY(),
                "getJointX", getJointX(), "getJointY", getJointY(),
                "getTileX", getTileX(), "getTileY", getTileY(),
                "getRotation", getRotation());
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
