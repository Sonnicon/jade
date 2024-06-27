package sonnicon.jade.entity.components.graphical;

import sonnicon.jade.Jade;
import sonnicon.jade.entity.components.Component;
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
            animation.free();
        }
        this.animation = animation;
    }

    public boolean isAnimating() {
        // playing with fire on float precision here
        return animation != null && animation.startTime + animation.duration >= Clock.getTickInterp();
    }

    public float getX() {
        return animation.getX(Clock.getTickInterp() - animation.startTime);
    }

    public float getY() {
        return animation.getY(Clock.getTickInterp() - animation.startTime);
    }

    public float getWidth() {
        return animation.getWidth(Clock.getTickInterp() - animation.startTime);
    }

    public float getHeight() {
        return animation.getHeight(Clock.getTickInterp() - animation.startTime);
    }

    public float getRotation() {
        return animation.getRotation(Clock.getTickInterp() - animation.startTime);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "animation", animation);
    }
}
