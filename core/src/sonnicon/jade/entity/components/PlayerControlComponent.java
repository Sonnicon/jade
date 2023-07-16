package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.game.Gamestate.State;
import sonnicon.jade.gui.StageIngame;

public class PlayerControlComponent extends Component {

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && ((StageIngame) State.ingame.getStage()).getControlledEntity() == null;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        ((StageIngame) State.ingame.getStage()).setControlledEntity(null);
    }

    @Override
    public boolean compare(Component other) {
        return other instanceof PlayerControlComponent && other.entity == entity;
    }
}
