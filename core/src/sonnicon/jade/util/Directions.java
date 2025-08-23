package sonnicon.jade.util;

import sonnicon.jade.game.IPosition;

import java.util.function.Consumer;

import static java.lang.Math.abs;

// Direction is stored as a byte
public class Directions {
    public static final byte NORTH = 1;
    public static final byte NORTHEAST = 1 << 1;
    public static final byte EAST = 1 << 2;
    public static final byte SOUTHEAST = 1 << 3;
    public static final byte SOUTH = 1 << 4;
    public static final byte SOUTHWEST = 1 << 5;
    public static final byte WEST = 1 << 6;
    public static final byte NORTHWEST = (byte) (1 << 7);

    // Compound directions for masking and comparison
    public static final byte HORIZONTAL = EAST | WEST;
    public static final byte VERTICAL = NORTH | SOUTH;
    public static final byte CARDINAL = HORIZONTAL | VERTICAL;
    public static final byte DIAGONAL = NORTHEAST | SOUTHEAST | SOUTHWEST | NORTHWEST;

    public static final byte NORTHWARD = NORTHWEST | NORTH | NORTHEAST;
    public static final byte EASTWARD = NORTHEAST | EAST | SOUTHEAST;
    public static final byte SOUTHWARD = SOUTHEAST | SOUTH | SOUTHWEST;
    public static final byte WESTWARD = SOUTHWEST | WEST | NORTHWEST;
    public static final byte HORIZONTALWARD = WESTWARD | EASTWARD;
    public static final byte VERTICALWARD = NORTHWARD | SOUTHWARD;

    public static final byte NONE = (byte) 0;
    public static final byte ALL = CARDINAL | DIAGONAL;

    public static boolean is(byte dir1, byte dir2) {
        return (dir1 & dir2) != 0;
    }

    public static void cardinals(Consumer<Byte> cons) {
        for (byte i = 0; i < 8; i += 2) {
            cons.accept((byte) (1 << i));
        }
    }

    public static void cardinals(Consumer2<Integer, Integer> cons) {
        cons.apply(0, 1);
        cons.apply(1, 0);
        cons.apply(0, -1);
        cons.apply(-1, 0);
    }

    public static void round(Consumer<Byte> cons) {
        for (byte i = 0; i < 8; i++) {
            cons.accept((byte) (1 << i));
        }
    }

    public static byte directionX(byte direction) {
        if (is(direction, Directions.EASTWARD)) {
            return 1;
        } else if (is(direction, Directions.WESTWARD)) {
            return -1;
        }
        return 0;
    }

    public static byte directionY(byte direction) {
        if (is(direction, Directions.NORTHWARD)) {
            return 1;
        } else if (is(direction, Directions.SOUTHWARD)) {
            return -1;
        }
        return 0;
    }

    public static byte rotate(byte dir, byte amount) {
        byte amt = (byte) Math.floorMod(amount, 8);
        return (byte) ((dir << amt) | ((dir & 0b011111111) >> (8 - amt)));
    }

    public static byte opposite(byte direction) {
        return rotate(direction, (byte) 4);
    }

    // Every direction that could be
    public static byte encodePossible(byte deltaX, byte deltaY) {
        byte result = ALL;

        if (deltaX > 0) {
            result &= Directions.EASTWARD;
        } else if (deltaX < 0) {
            result &= Directions.WESTWARD;
        }

        if (deltaY > 0) {
            result &= Directions.NORTHWARD;
        } else if (deltaY < 0) {
            result &= Directions.SOUTHWARD;
        }

        return result;
    }

    // Exactly the direction given
    public static byte encodePrecise(byte deltaX, byte deltaY) {
        byte d = encodePossible(deltaX, deltaY);
        if (deltaX == 0 && deltaY == 0) {
            return Directions.NONE;
        }
        if (deltaX * deltaY == 0) {
            return (byte) (d & Directions.CARDINAL);
        } else {
            return d;
        }
    }

    public static byte relate(float fromX, float fromY, float toX, float toY, float range) {
        byte result = ALL;
        if (fromX >= toX + range) result &= Directions.WESTWARD;
        if (toX >= fromX + range) result &= Directions.EASTWARD;
        if (fromY >= toY + range) result &= Directions.SOUTHWARD;
        if (toY >= fromY + range) result &= Directions.NORTHWARD;

        if (abs(fromX - toX) <= range || abs(fromY - toY) <= range) {
            result &= Directions.CARDINAL;
        }

        return result;
    }

    public static byte relate(float fromX, float fromY, float toX, float toY) {
        return relate(fromX, fromY, toX, toY, 0.001f);
    }

    public static byte relate(IPosition from, IPosition to, float range) {
        return relate(from.getX(), from.getY(), to.getX(), to.getY(), range);
    }

    public static byte relate(IPosition from, IPosition to) {
        return relate(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static byte relate(IPosition from, float toX, float toY) {
        return relate(from.getX(), from.getY(), toX, toY);
    }

    public static float magnitude(byte direction, float x, float y) {
        return x * directionX(direction) + y * directionY(direction);
    }

    public static byte toCardinalIndex(byte direction) {
        for (int i = 0; i < 8; i += 2) {
            if ((direction & (0b11 << i)) != 0) return (byte) (i / 2);
        }
        throw new RuntimeException();
    }

    public static byte toCompact(byte direction) {
        byte result = 0;
        if ((direction & NORTHWARD) != 0) result |= 1;
        if ((direction & EASTWARD) != 0) result |= 1 << 1;
        if ((direction & SOUTHWARD) != 0) result |= 1 << 2;
        if ((direction & WESTWARD) != 0) result |= 1 << 3;
        return result;
    }

    public static byte fromCompact(byte compact) {
        byte result = ALL;
        if ((compact & (1)) == 0) result &= NORTHWARD;
        if ((compact & (1 << 1)) == 0) result &= EASTWARD;
        if ((compact & (1 << 2)) == 0) result &= SOUTHWARD;
        if ((compact & (1 << 3)) == 0) result &= WESTWARD;
        return result;
    }

    public static String toString(byte direction) {
        StringBuilder result = new StringBuilder();
        if (is(direction, Directions.NORTH)) result.append("N ");
        if (is(direction, Directions.NORTHEAST)) result.append("NE ");
        if (is(direction, Directions.EAST)) result.append("E ");
        if (is(direction, Directions.SOUTHEAST)) result.append("SE ");
        if (is(direction, Directions.SOUTH)) result.append("S ");
        if (is(direction, Directions.SOUTHWEST)) result.append("SW ");
        if (is(direction, Directions.WEST)) result.append("W ");
        if (is(direction, Directions.NORTHWEST)) result.append("NW ");
        return result.toString().trim();
    }

    public static byte cardinalize(byte direction) {
        return (byte) ((direction | rotate(direction, (byte) -1) | rotate(direction, (byte) 1)) & Directions.CARDINAL);
    }
}
