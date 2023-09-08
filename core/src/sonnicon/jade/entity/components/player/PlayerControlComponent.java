package sonnicon.jade.entity.components.player;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.util.Events;
import sonnicon.jade.util.IComparable;

@EventGenerator(id = "EntityControlled", param = {Entity.class}, label = {"entity"})
@EventGenerator(id = "EntityUncontrolled", param = {Entity.class}, label = {"entity"})
public class PlayerControlComponent extends Component {
    private static Entity controlledEntity = null;
    public static final Events events = new Events();

    static {
        final EventTypes.EntityControlledEvent controlledHandler =
                (Entity e) -> ((StageIngame) Gamestate.State.ingame.getStage()).recreate();
        events.register(controlledHandler);
    }

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && getControlledEntity() == null;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        controlledEntity = entity;
        EventTypes.EntityControlledEvent.handle(entity.events, entity);
        EventTypes.EntityControlledEvent.handle(events, entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        controlledEntity = null;
        EventTypes.EntityUncontrolledEvent.handle(entity.events, entity);
        EventTypes.EntityUncontrolledEvent.handle(events, entity);
    }

    @Override
    public boolean compare(IComparable other) {
        return super.compare(other) && ((PlayerControlComponent) other).entity == entity;
    }

    public static Entity getControlledEntity() {
        return controlledEntity;
    }

    public static boolean isControlled(Entity entity) {
        return controlledEntity == entity;
    }
}
