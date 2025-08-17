package sonnicon.jade.entity.components.usage;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.util.Function3;

public class UseFunctionComponent extends Component implements IUsable {
    protected Function3<Entity, Float, Float, Boolean> func;

    public UseFunctionComponent() {

    }

    public UseFunctionComponent(Function3<Entity, Float, Float, Boolean> func) {
        setup(func);
    }

    private UseFunctionComponent setup(Function3<Entity, Float, Float, Boolean> func) {
        this.func = func;
        return this;
    }

    @Override
    public final boolean use(Entity user, float targetX, float targetY) {
        return func.apply(user, targetX, targetY);
    }

    //todo add copy, compare, and debug to all the components properly
    @Override
    public Component copy() {
        return ((UseFunctionComponent) super.copy()).setup(func);
    }
}
