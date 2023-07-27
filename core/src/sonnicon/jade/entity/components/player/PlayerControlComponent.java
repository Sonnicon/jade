package sonnicon.jade.entity.components.player;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Gamestate.State;
import sonnicon.jade.gui.StageIngame;

public class PlayerControlComponent extends Component {

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && isControlled(null);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(entity);
        entity.events.handle(EntityControlledEvent.class, entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(null);
        entity.events.handle(EntityUncontrolledEvent.class, entity);
    }

    @Override
    public boolean compare(Component other) {
        return other instanceof PlayerControlComponent && ((PlayerControlComponent) other).entity == entity;
    }

    public static boolean isControlled(Entity entity) {
        return ((StageIngame) State.ingame.getStage()).getControlledEntity() == entity;
    }

    public static final class EntityControlledEvent {
    }

    public static final class EntityUncontrolledEvent {
    }
}
