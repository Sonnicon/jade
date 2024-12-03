package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.Component;
import sonnicon.jade.entity.components.world.PositionBindComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.animation.Animation;
import sonnicon.jade.util.Events;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class AnimationComponent extends Component {
    protected Animation animation;
    public Events events;

    public void play(Animation animation) {
        animation.startTime = Clock.getTickInterp();
        if (this.animation != null && !this.animation.keepRef) {
            this.animation.free();
        }
        this.animation = animation;
    }

    public boolean isAnimating() {
        // playing with fire on float precision here
        return animation != null && animation.startTime + animation.duration >= Clock.getTickInterp();
    }

    public float getIndividualX() {
        return animation.getX(Clock.getTickInterp() - animation.startTime);
    }

    public float getIndividualY() {
        return animation.getY(Clock.getTickInterp() - animation.startTime);
    }

    public static float getNestedX(Entity entity) {
        float result = 0;

        // If we're bound to something, we take that animation offset as the base
        PositionBindComponent positionBindComponent = entity.getComponent(PositionBindComponent.class);
        if (positionBindComponent != null && positionBindComponent.follow != null) {
            result += getNestedX(positionBindComponent.follow);
        }

        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent != null && animationComponent.isAnimating()) {
            result += animationComponent.getIndividualX();
        }

        return result;
    }

    public static float getNestedY(Entity entity) {
        float result = 0;

        // If we're bound to something, we take that animation offset as the base
        PositionBindComponent positionBindComponent = entity.getComponent(PositionBindComponent.class);
        if (positionBindComponent != null && positionBindComponent.follow != null) {
            result += getNestedY(positionBindComponent.follow);
        }

        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent != null && animationComponent.isAnimating()) {
            result += animationComponent.getIndividualY();
        }

        return result;
    }

    public static float getNestedRotation(Entity entity) {
        float result = 0;

        // If we're bound to something, we take that animation offset as the base
        PositionBindComponent positionBindComponent = entity.getComponent(PositionBindComponent.class);
        if (positionBindComponent != null && positionBindComponent.follow != null) {
            result += getNestedRotation(positionBindComponent.follow);
        }

        AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
        if (animationComponent != null && animationComponent.isAnimating()) {
            result += animationComponent.getIndividualRotation();
        }

        return result;
    }

    public float getWidth() {
        return animation.getWidth(Clock.getTickInterp() - animation.startTime);
    }

    public float getHeight() {
        return animation.getHeight(Clock.getTickInterp() - animation.startTime);
    }

    public float getIndividualRotation() {
        return animation.getRotation(Clock.getTickInterp() - animation.startTime);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "animation", animation);
    }
}
