package sonnicon.jade.util;

import sonnicon.jade.content.Content;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.world.World;

public class DebugTarget implements IPositionMoving {
    public float x, y, rotation;

    public DebugTarget() {
        this(Float.NaN, Float.NaN);
    }

    public DebugTarget(IPosition other) {
        forceMoveTo(other);
    }

    public DebugTarget(float x, float y) {
        forceMoveTo(x, y);
    }

    @Override
    public void forceMoveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean rotateTo(float degrees) {
        this.rotation = degrees;
        return true;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public World getWorld() {
        return Content.world;
    }
}
