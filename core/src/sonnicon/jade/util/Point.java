package sonnicon.jade.util;

import java.util.ArrayList;

public class Point {
    public static byte getXFromPoint(short point) {
        return (byte) ((point >> 8) & 0xFF);
    }

    public static byte getYFromPoint(short point) {
        return (byte) (point & 0xFF);
    }

    public static short getPoint(byte x, byte y) {
        return (short) ((x << 8) | (y & 0xFF));
    }

    public static short sumPoints(short a, short b) {
        // we can't just add them because signed
        byte x = (byte) (getXFromPoint(a) + getXFromPoint(b));
        byte y = (byte) (getYFromPoint(a) + getYFromPoint(b));
        return getPoint(x, y);
    }

    public static short addToPoint(short point, byte x, byte y) {
        byte getX = getXFromPoint(point);
        byte nx = (byte) (getX + x);
        byte getY = getYFromPoint(point);
        byte ny = (byte) (getY + y);
        return getPoint(nx, ny);
    }

    public static void pointListToDeltas(ArrayList<Short> inplace) {
        for (int i = inplace.size() - 1; i > 0; i--) {
            short from = inplace.get(i - 1);
            short to = inplace.get(i);

            byte dx = (byte) (getXFromPoint(to) - getXFromPoint(from));
            byte dy = (byte) (getYFromPoint(to) - getYFromPoint(from));

            inplace.set(i, getPoint(dx, dy));
        }
    }

    public static String pointToString(short point) {
        return getXFromPoint(point) + "," + getYFromPoint(point);
    }
}
