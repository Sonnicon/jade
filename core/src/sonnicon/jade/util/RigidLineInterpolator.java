package sonnicon.jade.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

// Interpolate rigid line (for swords) "anchored" to a point, and pointing to another
public class RigidLineInterpolator {
    public float startAX, startAY, startRotation;
    protected float originX, originY;
    public float endMidX, endMidY, endRotation;
    protected float length;

    public void set(float originX, float originY, float startAX, float startAY, float startRotation, float length, float endBX, float endBY) {
        this.startAX = startAX - originX;
        this.startAY = startAY - originY;
        this.startRotation = startRotation;
        this.originX = originX;
        this.originY = originY;
        this.length = length;

        float diffAX = endBX - originX;
        float diffAY = endBY - originY;
        float diffALength = (float) Math.sqrt(diffAX * diffAX + diffAY * diffAY);
        float fracNeededA = (length - diffALength) / diffALength;
        float dAX = originX - fracNeededA * diffAX;
        float dAY = originY - fracNeededA * diffAY;

        this.endMidX = (dAX + endBX) * 0.5f - originX;
        this.endMidY = (dAY + endBY) * 0.5f - originY;
        this.endRotation = 270f - MathUtils.atan2Deg((dAY - endBY), (dAX - endBX));
    }

    public float interpolateX(float amount) {
        return MathUtils.lerp(startAX, endMidX, amount);
    }

    public float interpolateY(float amount) {
        return MathUtils.lerp(startAY, endMidY, amount);
    }

    public float interpolateRotation(float amount) {
        return Utils.lerpDeg(amount, startRotation, endRotation);
    }

    public float getLength() {
        float a = startAX - endMidX;
        float b = startAY - endMidY;
        return (float) Math.sqrt(a * a + b * b);
    }

    public float getRotationLength() {
        return Utils.lengthDeg(startRotation, endRotation);
    }

    public void interpolateTipPosition(float amount, Vector2 result) {
        //todo
        Vector2 direction = new Vector2(1f, 0f);
        direction.setAngleDeg(90f - interpolateRotation(amount));
        direction.setLength(24f);
        result.set(interpolateX(amount) + originX, interpolateY(amount) + originY);
        result.add(direction);
    }
}
