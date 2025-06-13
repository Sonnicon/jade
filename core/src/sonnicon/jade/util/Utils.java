package sonnicon.jade.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import sonnicon.jade.game.IPosition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    public static <T> HashSet<T> setFrom(T... objs) {
        return (HashSet<T>) Arrays.stream(objs).collect(Collectors.toSet());
    }

    public static <T> HashSet<T> setExtendFrom(HashSet<T> set, T... objs) {
        set.addAll(Arrays.asList(objs));
        return set;
    }

    public static <T, K> HashMap<T, K> mapFrom(Object... objs) {
        HashMap<T, K> result = new HashMap<>();
        for (int i = 0; i < objs.length; i += 2) {
            result.put((T) objs[i], (K) objs[i + 1]);
        }
        return result;
    }

    public static <T, K> Map<T, K> mapExtendFrom(Map<T, K> map, Object... objs) {
        for (int i = 0; i < objs.length; i += 2) {
            map.put((T) objs[i], (K) objs[i + 1]);
        }
        return map;
    }

    public static boolean overlapsSquare(int x1, int y1, short r1, int x2, int y2, short r2) {
        return ((x1 + r1 > x2 - r2) ^ (x1 - r1 > x2 + r2)) &&
                (y1 + r1 > y2 - r2) ^ (y1 - r1 > y2 + r2);
    }

    public static boolean overlapsSquare(float x1, float y1, float r1, float x2, float y2, float r2) {
        return ((x1 + r1 > x2 - r2) ^ (x1 - r1 > x2 + r2)) &&
                (y1 + r1 > y2 - r2) ^ (y1 - r1 > y2 + r2);
    }

    public static float pythag(float x, float y) {
        float a = x * x + y * y;
        return (float) Math.sqrt(a);
    }

    public static float mat3mul(Matrix3 matrix, byte row, float v1, float v2, float v3) {
        byte offset = (byte) (row * 3);
        return matrix.val[offset] * v1 + matrix.val[offset + 1] * v2 + matrix.val[offset + 2] * v3;
    }

    public static float lerpX(IPosition from, IPosition to, float value) {
        return MathUtils.lerp(from.getX(), to.getX(), value);
    }

    public static float lerpY(IPosition from, IPosition to, float value) {
        return MathUtils.lerp(from.getY(), to.getY(), value);
    }
}
