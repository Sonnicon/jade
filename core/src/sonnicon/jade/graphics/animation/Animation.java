package sonnicon.jade.graphics.animation;

import sonnicon.jade.entity.components.graphical.AnimationComponent;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.Map;

public abstract class Animation implements ObjectPool.IPooledObject, IDebuggable {
    public float startTime, duration;
    public boolean keepRef;

    protected Animation init(float duration) {
        this.duration = duration;
        return this;
    }

    public Animation play(AnimationComponent ac) {
        ac.play(this);
        return this;
    }

    public void stop() {
        startTime = Float.NaN;
    }

    public Animation reset() {
        keepRef = false;
        return this;
    }

    public float getX(float time) {
        return 0;
    }

    public float getY(float time) {
        return 0;
    }

    public float getWidth(float time) {
        return 0;
    }

    public float getHeight(float time) {
        return 0;
    }

    public float getRotation(float time) {
        return 0;
    }

    public static <T extends Animation> T obtain(Class<T> type) {
        return (T) ObjectPool.obtain(type).reset();
    }

    public final Animation keepRef() {
        keepRef = true;
        return this;
    }

    public final void free() {
        ObjectPool.free(this);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("startTime", startTime, "duration", duration, "keepRef", keepRef);
    }
}