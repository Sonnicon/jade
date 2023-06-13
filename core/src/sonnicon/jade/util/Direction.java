package sonnicon.jade.util;

import java.util.function.BiConsumer;

public class Direction {
    public static final byte NORTH = 1;
    public static final byte EAST = 1 << 1;
    public static final byte SOUTH = 1 << 2;
    public static final byte WEST = 1 << 3;

    public static void cardinals(BiConsumer<Integer, Integer> cons) {
        cons.accept(0, 1);
        cons.accept(1, 0);
        cons.accept(0, -1);
        cons.accept(-1, 0);
    }

    public static short directionX(byte direction) {
        return (short)(((direction & EAST) > 0) ? 1 : ((direction & WEST) > 0) ? -1 : 0);
    }

    public static short directionY(byte direction) {
        return (short)(((direction & NORTH) > 0) ? 1 : ((direction & SOUTH) > 0) ? -1 : 0);
    }

    public static byte rotate(byte direction, byte amount) {
        amount = (byte) Math.floorMod(amount, 4);
        return (byte) ((byte) ((direction << amount) | (direction >> (4 - amount))) & 0b1111);
    }
}
