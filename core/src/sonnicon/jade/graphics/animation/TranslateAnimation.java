package sonnicon.jade.graphics.animation;

import sonnicon.jade.util.Utils;

import java.util.Map;

public class TranslateAnimation extends Animation {
    protected float startX, startY;
    protected float dx, dy;

    public TranslateAnimation init(float startX, float startY, float endX, float endY, float duration) {
        super.init(duration);
        this.startX = startX;
        this.startY = startY;
        this.dx = (endX - startX) / duration;
        this.dy = (endY - startY) / duration;
        return this;
    }

    @Override
    public float getX(float time) {
        return startX + dx * time;
    }

    @Override
    public float getY(float time) {
        return startY + dy * time;
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapExtendFrom(super.debugProperties(), "startX", startX, "startY", startY, "dx", dx, "dy", dy);
    }
}
