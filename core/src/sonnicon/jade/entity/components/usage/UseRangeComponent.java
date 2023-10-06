package sonnicon.jade.entity.components.usage;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.IUsable;
import sonnicon.jade.util.IComparable;
import sonnicon.jade.util.Utils;

public abstract class UseRangeComponent extends Component implements IUsable {
    public float rangeMin = 0f, rangeMax = Float.MAX_VALUE;

    public UseRangeComponent() {

    }

    public UseRangeComponent(float rangeMin, float rangeMax) {
        setup(rangeMin, rangeMax);
    }

    private UseRangeComponent setup(float rangeMin, float rangeMax) {
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        return this;
    }

    @Override
    public final void use(Entity user, int jointX, int jointY) {
        PositionComponent positionComponent;
        if (user == null ||
                (positionComponent = user.getComponent(PositionComponent.class)) == null) {
            throw new IllegalArgumentException();
        }
        int dx = jointX - positionComponent.getJointX();
        int dy = jointY - positionComponent.getJointY();
        float distance = Utils.pythag(dx, dy);
        if (distance <= rangeMin || distance >= rangeMax) {
            return;
        }
        use(user, distance, (float) Math.toDegrees(((MathUtils.atan2(dy, -dx) + Math.PI * 1.5f) % (Math.PI * 2f))));
    }

    public abstract void use(Entity user, float dist, float angle);

    @Override
    public Component copy() {
        return ((UseRangeComponent) super.copy()).setup(rangeMin, rangeMax);
    }

    @Override
    public boolean compare(IComparable other) {
        if (!super.compare(other)) return false;
        UseRangeComponent o = (UseRangeComponent) other;
        return Math.abs(o.rangeMin - rangeMin) < 0.01f && Math.abs(o.rangeMax - rangeMax) < 0.01f;
    }
}
