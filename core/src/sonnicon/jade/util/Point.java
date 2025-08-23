package sonnicon.jade.util;

import sonnicon.jade.content.Content;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.IPositionMoving;
import sonnicon.jade.world.World;

import java.util.Map;

public class Point implements IPositionMoving, ObjectPool.IPooledObject, IDebuggable {
    public float x, y, rotation;
    public World world;

    public Point() {

    }

    public static Point at(float x, float y) {
        return at(x, y, 0f, Content.world);
    }

    public static Point at(float x, float y, float rotation) {
        return at(x, y, rotation, Content.world);
    }

    public static Point at(float x, float y, float rotation, World world) {
        Point p = ObjectPool.obtain(Point.class);
        p.moveTo(x, y);
        p.rotateTo(rotation);
        p.world = world;
        return p;
    }

    public static Point at(IPosition position) {
        return at(position.getX(), position.getY(), position.getRotation(), position.getWorld());
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

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "x", getX(),
                "y", getY(),
                "rotation", getRotation(),
                "world", getWorld()
        );
    }
}
