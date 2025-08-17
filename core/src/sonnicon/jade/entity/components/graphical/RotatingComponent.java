package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.Clock;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class RotatingComponent extends Component implements Clock.IOnFrame {
    private float rotationSpeed;

    public RotatingComponent(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        Clock.register(this);
    }

    @Override
    public void removeFromEntity(Entity entity) {
        super.removeFromEntity(entity);
        Clock.unregister(this);
    }

    //todo do on ticks
    @Override
    public void onFrame(float delta) {
        entity.rotateBy(rotationSpeed * delta);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "rotationSpeed", rotationSpeed);
    }
}
