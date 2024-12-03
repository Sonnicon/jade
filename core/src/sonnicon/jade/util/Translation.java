package sonnicon.jade.util;

import com.badlogic.gdx.math.MathUtils;
import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.graphics.animation.Animation;
import sonnicon.jade.graphics.animation.TranslateAnimation;

import java.util.Map;

public class Translation implements ObjectPool.IPooledObject, IDebuggable {
    private boolean enableFlat, enableRotate, enableFollowRotation;
    private float flatX, flatY, rotation;
    private float relX, relY;

    private float diffX, diffY;
    private float resX, resY, resR;

    private float markX, markY;

    public void apply(PositionComponent positionComponent) {
        resX = positionComponent.getFloatingX() + diffX;
        resY = positionComponent.getFloatingY() + diffY;

        resR = positionComponent.getRotation();
        if (enableFollowRotation) {
            resR += rotation;
        }
    }

    protected float getDiffX() {
        return diffX;
    }

    protected float getDiffY() {
        return diffY;
    }

    public void free() {
        ObjectPool.free(this);
    }

    public static Translation obtain() {
        Translation t = ObjectPool.obtain(Translation.class);
        t.enableFlat = false;
        t.enableRotate = false;
        t.enableFollowRotation = false;
        // don't really care about resetting actual values
        return t;
    }

    public Translation setFlatOffset(float x, float y) {
        flatX = x;
        flatY = y;
        enableFlat = true;
        recalculate();
        return this;
    }

    public Translation setRotatedOffset(float x, float y) {
        relX = x;
        relY = y;
        enableRotate = true;
        recalculate();
        return this;
    }

    public Translation setFollowRotationOffset(float rotation) {
        this.rotation = rotation;
        enableFollowRotation = true;
        recalculate();
        return this;
    }

    public TranslateAnimation getAnimation(float duration) {
        TranslateAnimation anim = Animation.obtain(TranslateAnimation.class);
        anim.init(0, 0, getDiffX() - markX, getDiffY() - markY, duration);
        return anim;
    }

    public void recalculate() {
        float dx = 0, dy = 0;

        if (enableRotate) {
            dx += relX * MathUtils.cosDeg(rotation) + relY * MathUtils.sinDeg(rotation);
            dy += relX * -MathUtils.sinDeg(rotation) + relY * MathUtils.cosDeg(rotation);
        }

        if (enableFlat) {
            dx += flatX;
            dy += flatY;
        }

        this.diffX = dx;
        this.diffY = dy;
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

    public float getResX() {
        return resX;
    }

    public float getResY() {
        return resY;
    }

    public float getResR() {
        return resR;
    }

    // for animations
    public void mark() {
        this.markX = diffX;
        this.markY = diffY;
    }

    public Translation copy() {
        Translation other = ObjectPool.obtain(Translation.class);
        other.enableFlat = enableFlat;
        other.enableRotate = enableRotate;
        other.enableFollowRotation = enableFollowRotation;
        other.resX = resX;
        other.resY = resY;
        other.resR = resR;
        other.markX = markX;
        other.markY = markY;
        other.diffX = diffX;
        other.diffY = diffY;
        other.flatX = flatX;
        other.flatY = flatY;
        other.rotation = rotation;
        other.relX = relX;
        other.relY = relY;
        return other;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("enableFlat", enableFlat, "enableRotate", enableRotate,
                "enableFollowRotation", enableFollowRotation, "flatX", flatX, "flatY", flatY,
                "rotation", rotation, "diffX", diffX, "diffY", diffY, "relX", relX, "relY", relY, "resX", resX,
                "resY", resY, "resR", resR, "markX", markX, "markY", markY);
    }
}
