package sonnicon.jade.util;

import com.badlogic.gdx.math.Interpolation;

public class Interpolations {
    public static float unapply(Interpolation.Pow interp, double power, float value) {
        if (value <= 0.5f) {
            return (float) Math.pow(value / Math.pow(2f, power - 1), 1f / power);
        } else {
            float m = power % 2 == 0 ? -2f : 2f;
            return -(float) (Math.pow((value - 1f) * m, 1f / power) / 2f + 1f);
        }
    }
}
