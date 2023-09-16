package sonnicon.jade.input.actions;

import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.game.Actions;
import sonnicon.jade.util.Utils;

import java.util.Map;

public class CharacterMoveAction extends Actions.Action {
    private PositionComponent target;
    private short moveX, moveY;

    public CharacterMoveAction set(PositionComponent target, short moveX, short moveY) {
        this.target = target;
        this.moveX = moveX;
        this.moveY = moveY;
        return this;
    }

    @Override
    public void finish() {
        super.finish();
        target.tryMoveByPos(moveX, moveY);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "target", target, "moveX", moveX, "moveY", moveY);
    }
}