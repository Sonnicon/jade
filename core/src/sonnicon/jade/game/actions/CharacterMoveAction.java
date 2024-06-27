package sonnicon.jade.game.actions;

import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.entity.components.world.SubtilePositionComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.graphics.animation.Animation;
import sonnicon.jade.graphics.animation.TranslateAnimation;
import sonnicon.jade.util.Utils;
import sonnicon.jade.world.Tile;

import java.util.Map;

public class CharacterMoveAction extends Actions.Action {
    protected SubtilePositionComponent target;
    protected short moveX, moveY;
    protected TranslateAnimation animation;

    public CharacterMoveAction set(SubtilePositionComponent target, short moveX, short moveY) {
        this.target = target;
        this.moveX = moveX;
        this.moveY = moveY;
        return this;
    }

    @Override
    public void onStart() {
        if (!target.canMoveByPos(moveX, moveY)) {
            interrupt();
            return;
        }

        AnimationComponent ac = target.entity.getComponent(AnimationComponent.class);
        if (ac != null) {
            animation = Animation.obtain(TranslateAnimation.class);
            float duration = timeFinish - Clock.getTickNum();
            animation.init(0, 0, moveX * Tile.SUBTILE_DELTA, moveY * Tile.SUBTILE_DELTA, duration);
            ac.play(animation);
        }
    }

    @Override
    public void onFinish() {
        if (animation != null) {
            animation.stop();
        }
        target.tryMoveBy(moveX, moveY);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "target", target, "moveX", moveX, "moveY", moveY, "translateAnimation", animation);
    }
}