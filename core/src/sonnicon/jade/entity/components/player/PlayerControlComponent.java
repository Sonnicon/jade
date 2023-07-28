package sonnicon.jade.entity.components.player;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Gamestate.State;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.StageIngame;

@EventGenerator(id = "EntityControlled", param = {Entity.class}, label = {"entity"})
@EventGenerator(id = "EntityUncontrolled", param = {Entity.class}, label = {"entity"})
public class PlayerControlComponent extends Component {

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && isControlled(null);
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(entity);
        EventTypes.EntityControlledEvent.handle(entity.events, entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(null);
        EventTypes.EntityUncontrolledEvent.handle(entity.events, entity);
    }

    @Override
    public boolean compare(Component other) {
        return other instanceof PlayerControlComponent && ((PlayerControlComponent) other).entity == entity;
    }

    public static boolean isControlled(Entity entity) {
        return ((StageIngame) State.ingame.getStage()).getControlledEntity() == entity;
    }
}
