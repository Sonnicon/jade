package sonnicon.jade.game.actions;

import sonnicon.jade.entity.components.world.SubtilePositionComponent;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class CharacterMoveAction extends Actions.Action {
    private SubtilePositionComponent target;
    private short moveX, moveY;

    public CharacterMoveAction set(SubtilePositionComponent target, short moveX, short moveY) {
        this.target = target;
        this.moveX = moveX;
        this.moveY = moveY;
        return this;
    }

    @Override
    public void finish() {
        super.finish();

        target.tryMoveBy(moveX, moveY);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "target", target, "moveX", moveX, "moveY", moveY);
    }
}