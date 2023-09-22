package sonnicon.jade.entity.components.world;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

import java.util.HashSet;

public class PositionBindComponent extends Component {
    public Entity follow;
    //todo don't do this
    private static boolean suppress = false;

    private final EventTypes.EntityMoveEvent ON_MOVE_FOLLOWED = this::moveTo;

    private static final EventTypes.EntityMoveEvent ON_MOVE_THIS = (Entity e) -> {
        if (!suppress) {
            e.getComponentFuzzy(PositionBindComponent.class).detach();
        }
    };

    public PositionBindComponent() {

    }

    public PositionBindComponent(Entity target) {
        setup(target);
    }

    public PositionBindComponent setup(Entity target) {
        attach(target);
        return this;
    }

    public void attach(Entity target) {
        if (!target.hasComponent(PositionComponent.class)) {
            throw new IllegalArgumentException();
        }

        if (follow == target) {
            return;
        }
        if (entity != null) {
            detach();
        }
        follow = target;
        if (entity != null) {
            attach();
        }
    }

    private void attach() {
        if (follow != null) {
            entity.events.register(ON_MOVE_THIS);
            follow.events.register(ON_MOVE_FOLLOWED);
            moveTo(follow);
        }
    }

    public void detach() {
        if (follow != null) {
            entity.events.unregister(ON_MOVE_THIS);
            follow.events.unregister(ON_MOVE_FOLLOWED);
            follow = null;
        }
    }

    private void moveTo(Entity e) {
        suppress = true;
        entity.getComponentFuzzy(PositionComponent.class).moveToOther(e.getComponent(PositionComponent.class));
        suppress = false;
    }

    @Override
    public void addToEntity(Entity entity) {
        super.addToEntity(entity);
        attach();
    }

    @Override
    public void removeFromEntity(Entity entity) {
        detach();
        super.removeFromEntity(entity);
    }

    @Override
    public HashSet<Class<? extends Component>> getDependencies() {
        return Utils.setFrom(PositionComponent.class);
    }

    @Override
    public Component copy() {
        return ((PositionBindComponent) super.copy()).setup(follow);
    }

    @Override
    public boolean compare(IComparable other) {
        if (!super.compare(other)) {
            return false;
        }
        PositionBindComponent o = (PositionBindComponent) other;
        return follow == o.follow;
    }
}
