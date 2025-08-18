package sonnicon.jade.util;

import sonnicon.jade.content.Content;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.world.World;

public class PointPosition implements IPositionMoving, ObjectPool.IPooledObject {
    public float x, y, rotation;

    public PointPosition() {

    }

    public static PointPosition at(float x, float y) {
        return at(x, y, 0f);
    }

    public static PointPosition at(float x, float y, float rotation) {
        PointPosition p = ObjectPool.obtain(PointPosition.class);
        p.moveTo(x, y);
        p.rotateTo(rotation);
        return p;
    }

    @Override
    public void onObtained() {
        moveTo(Float.NaN, Float.NaN);
        rotateTo(0f);
    }

    @Override
    public void moveTo(float x, float y) {
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
