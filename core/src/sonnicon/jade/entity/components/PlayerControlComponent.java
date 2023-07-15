package sonnicon.jade.entity.components;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.gui.Gui;

public class PlayerControlComponent extends Component {

    @Override
    public boolean canAddToEntity(Entity entity) {
        return super.canAddToEntity(entity) && Gui.stageIngame.getControlledEntity() == null;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Gui.stageIngame.setControlledEntity(entity);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Gui.stageIngame.setControlledEntity(null);
    }

    @Override
    public boolean compare(Component other) {
        return other instanceof PlayerControlComponent && other.entity == entity;
    }
}
