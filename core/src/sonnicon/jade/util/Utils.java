package sonnicon.jade.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import sonnicon.jade.Jade;
import sonnicon.jade.game.IPosition;
import sonnicon.jade.game.collision.IBoundRectangle;
import sonnicon.jade.game.collision.Quadtree;
import sonnicon.jade.graphics.particles.BoxParticle;
import sonnicon.jade.graphics.particles.LineParticle;
import sonnicon.jade.world.Chunk;

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

    public static float lerpDeg(float amount, float min, float max) {
        if (max > min) {
            return min + lengthDeg(min, max) * amount;
        } else {
            return min - lengthDeg(max, min) * amount;
        }
    }

    public static float lengthDeg(float min, float max) {
        if (min > max) {
            return lengthDeg(max, min);
        }
        if (Math.abs(min - max) > 180f) {
            return max - min - 360f;
        }
        return max - min;
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

    public static boolean isLinePointClockwise(float x1, float y1, float x2, float y2, float xp, float yp) {
        return (xp * y2 + x1 * yp + y1 * x2 - y1 * xp - yp * x2 - x1 * y2) > 0f;
    }

    private static boolean isIntersectingHalfResult(IBoundRectangle rect1, IPosition pos1,
                                                    IBoundRectangle rect2, IPosition pos2) {
        //todo make sure this is getting optimized, or don't allocate

        Vector2 vec2 = pos2.getPosition(new Vector2());
        vec2.sub(pos1.getX(), pos1.getY());
        float rotation1 = pos1.getRotation();
        float rotation2 = pos2.getRotation();
        vec2.rotateDeg(rotation1);

        float rect1halfWidth = rect1.getWidth() / 2f;
        float rect1halfHeight = rect1.getHeight() / 2f;

        // Decide which rect1 edge to use
        byte edge1directions = Directions.relate(0f, 0f, vec2.x, vec2.y);
        edge1directions = Directions.cardinalize(edge1directions);

        byte edge1 = Directions.NONE;
        byte edge2 = Directions.NONE;
        for (int i = 0; i < 4; i++) {
            if (Directions.is(edge1directions, (byte) (1 << (i * 2)))) {
                if (edge1 == Directions.NONE) {
                    edge1 = (byte) (1 << (i * 2));
                } else {
                    edge2 = (byte) (1 << (i * 2));
                    break;
                }
            }
        }

        boolean[] isSeparatingAxis = {edge1 != Directions.NONE, edge2 != Directions.NONE};
        for (int cornerNum = 0; cornerNum < 4; cornerNum++) {

            final float[] corners = {1f, 1f, -1f, -1f, 1f};
            Vector2 corner = new Vector2(corners[cornerNum], corners[cornerNum + 1]);
            corner.scl(rect2.getWidth() * 0.5f, rect2.getHeight() * 0.5f);
            corner.rotateDeg(rotation1 - rotation2);
            corner.add(vec2.x, vec2.y);

            for (int i = 0; i < 2; i++) {
                if (!isSeparatingAxis[i]) continue;

                byte edge = (i == 0) ? edge1 : edge2;

                if (Directions.is(edge, Directions.HORIZONTAL)) {
                    float projEdgeX = corner.x * Directions.directionX(edge);
                    isSeparatingAxis[i] = projEdgeX >= rect1halfWidth;
                } else {
                    float projEdgeY = corner.y * Directions.directionY(edge);
                    isSeparatingAxis[i] = projEdgeY >= rect1halfHeight;
                }
            }

            if (!isSeparatingAxis[0] && !isSeparatingAxis[1]) {
                return true;
            }
        }

        return false;
    }

    public static boolean isIntersectingAABB(IBoundRectangle rect1, IPosition pos1,
                                             IBoundRectangle rect2, IPosition pos2) {
        //todo handle 90/180/270
        assert Math.abs(pos1.getRotation()) < 0.0001f;
        assert Math.abs(pos2.getRotation()) < 0.0001f;

        return Math.abs(pos1.getX() - pos2.getX()) < (rect1.getWidth() + rect2.getWidth()) * 0.5f &&
                Math.abs(pos1.getY() - pos2.getY()) < (rect1.getHeight() + rect2.getHeight()) * 0.5f;
    }

    public static boolean isIntersecting(IBoundRectangle rect1, IPosition pos1, IBoundRectangle rect2, IPosition pos2) {
        boolean collided;
        if (Math.abs(pos1.getRotation()) < 0.0001f && Math.abs(pos2.getRotation()) < 0.0001f) {
            collided = isIntersectingAABB(rect1, pos1, rect2, pos2);
        } else {
            collided = isIntersectingHalfResult(rect1, pos1, rect2, pos2)
                    && isIntersectingHalfResult(rect2, pos2, rect1, pos1);
        }

        if (collided && !(pos1 instanceof Quadtree) && !(pos2 instanceof Quadtree) && !(pos1 instanceof Chunk) && !(pos2 instanceof Chunk)) {
            Color drawColor = Color.GREEN.cpy();
            LineParticle lp = Jade.renderer.particles.createParticle(LineParticle.class, pos1.getX(), pos1.getY());
            lp.color = drawColor;
            lp.thickness = 2f;
            lp.setDest(pos2.getX(), pos2.getY());
            BoxParticle bp1 = Jade.renderer.particles.createParticle(BoxParticle.class, pos1.getX(), pos1.getY());
            bp1.setColor(drawColor);
            bp1.setSize(rect1.getWidth(), rect1.getHeight(), pos1.getRotation());
            BoxParticle bp2 = Jade.renderer.particles.createParticle(BoxParticle.class, pos2.getX(), pos2.getY());
            bp2.setColor(drawColor);
            bp2.setSize(rect2.getWidth(), rect2.getHeight(), pos2.getRotation());
        } else {
            Color drawColor = Color.RED.cpy();
            LineParticle lp = Jade.renderer.particles.createParticle(LineParticle.class, pos1.getX(), pos1.getY());
            lp.color = drawColor;
            lp.thickness = 0.5f;
            lp.setDest(pos2.getX(), pos2.getY());
        }

        return collided;
    }

    public static float normalizeAngle(float angle) {
        return (angle % 360f + 360f) % 360f;
    }
}
