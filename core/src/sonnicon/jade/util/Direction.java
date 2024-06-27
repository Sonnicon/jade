package sonnicon.jade.util;

import sonnicon.jade.entity.components.world.PositionComponent;
import sonnicon.jade.world.Tile;

import java.util.function.Consumer;

import static java.lang.Math.abs;

public class Direction {
    public static final byte NORTH = 1;
    public static final byte EAST = 1 << 1;
    public static final byte SOUTH = 1 << 2;
    public static final byte WEST = 1 << 3;

    public static final byte ALL = NORTH | EAST | SOUTH | WEST;
    public static final byte HORIZONTAL = EAST | WEST;
    public static final byte VERTICAL = NORTH | SOUTH;

    public static void cardinals(Consumer2<Integer, Integer> cons) {
        cons.apply(0, 1);
        cons.apply(1, 0);
        cons.apply(0, -1);
        cons.apply(-1, 0);
    }

    public static void cardinals(Consumer<Byte> cons) {
        for (byte i = 0; i < 4; i++) {
            cons.accept((byte) (1 << i));
        }
    }

    public static void round(Consumer<Byte> cons) {
        for (byte i = 0; i < 4; i++) {
            cons.accept((byte) (1 << i));
            byte r = (byte) (1 << (i + 1));
            cons.accept((byte) (((1 << i) | r | r >>> 4) & Direction.ALL));

        }
    }

    public static short directionX(byte direction) {
        return (short) (((direction & EAST) > 0) ? 1 : ((direction & WEST) > 0) ? -1 : 0);
    }

    public static short directionY(byte direction) {
        return (short) (((direction & NORTH) > 0) ? 1 : ((direction & SOUTH) > 0) ? -1 : 0);
    }

    public static byte rotate(byte direction, byte amount) {
        amount = (byte) Math.floorMod(amount, 4);
        return (byte) ((byte) ((direction << amount) | (direction >>> (4 - amount))) & 0b1111);
    }

    public static byte flatten(byte direction) {
        if ((direction & EAST) > 0 && (direction & WEST) > 0) {
            direction ^= EAST | WEST;
        }
        if ((direction & NORTH) > 0 && (direction & SOUTH) > 0) {
            direction ^= NORTH | SOUTH;
        }
        return direction;
    }

    public static byte encode(byte deltaX, byte deltaY) {
        byte result = 0;
        if (deltaX != 0) {
            result |= deltaX > 0 ? Direction.EAST : Direction.WEST;
        }
        if (deltaY != 0) {
            result |= deltaY > 0 ? Direction.NORTH : Direction.SOUTH;
        }
        return result;
    }

    public static byte relate(float fromX, float fromY, float toX, float toY) {
        byte result = 0;
        if (abs(toX - fromX) > 0.001f) result |= toX > fromX ? EAST : WEST;
        if (abs(toY - fromY) > 0.001f) result |= toY > fromY ? NORTH : SOUTH;
        return result;
    }

    public static byte relate(Tile from, Tile to) {
        return relate(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static byte relate(PositionComponent from, PositionComponent to, float range) {
        byte result = 0;
        if (abs(to.getDrawX() - from.getDrawX()) >= range) {
            result |= to.getDrawX() > from.getDrawX() ? EAST : WEST;
        }
        if (abs(to.getDrawY() - from.getDrawY()) >= range) {
            result |= to.getDrawY() > from.getDrawY() ? NORTH : SOUTH;
        }
        return result;
    }

    public static byte relate(PositionComponent from, PositionComponent to) {
        return relate(from.getDrawX(), from.getDrawY(), to.getDrawX(), to.getDrawY());
    }
}
