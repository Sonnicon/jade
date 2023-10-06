package sonnicon.jade.util;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.components.world.PositionComponent;

public class Translation implements ObjectPool.IPooledObject {
    private boolean enableFlat, enableRotate, enableFollowRotation;
    private float dx, dy, dr, drx, dry;

    private static float resX, resY, resR;

    public void apply(PositionComponent positionComponent) {
        resX = positionComponent.getDrawX();
        resY = positionComponent.getDrawY();
        resR = positionComponent.getRotation();

        if (enableRotate) {
            resX += drx * MathUtils.cosDeg(dr) + dry * MathUtils.sinDeg(dr);
            resY += drx * -MathUtils.sinDeg(dr) + dry * MathUtils.cosDeg(dr);
        }

        if (enableFlat) {
            resX += dx;
            resY += dy;
        }

        if (enableFollowRotation) {
            resR += dr;
        }
    }

    @Override
    public void onFree() {
        resetFlatOffset();
        resetRotatedOffset();
        resetFollowRotationOffset();
    }

    public void free() {
        ObjectPool.free(this);
    }

    public static Translation obtain() {
        return ObjectPool.obtain(Translation.class);
    }

    public Translation setFlatOffset(float x, float y) {
        dx = x;
        dy = y;
        enableFlat = true;
        return this;
    }

    public Translation resetFlatOffset() {
        enableFlat = false;
        return this;
    }

    public Translation setRotatedOffset(float x, float y) {
        drx = x;
        dry = y;
        enableRotate = true;
        return this;
    }

    public Translation resetRotatedOffset() {
        enableRotate = false;
        return this;
    }

    public Translation setFollowRotationOffset(float rotation) {
        dr = rotation;
        enableFollowRotation = true;
        return this;
    }

    public Translation resetFollowRotationOffset() {
        enableFollowRotation = false;
        return this;
    }

    public boolean isFlat() {
        return enableFlat;
    }

    public boolean isRotate() {
        return enableRotate;
    }

    public boolean followRotation() {
        return enableFollowRotation;
    }

    public static float getResX() {
        return resX;
    }

    public static float getResY() {
        return resY;
    }

    public static float getResR() {
        return resR;
    }
}
