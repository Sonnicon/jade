package sonnicon.jade.util;

public class JMath {
    public static boolean overlapsSquare(int x1, int y1, short r1, int x2, int y2, short r2) {
        return ((x1 + r1 > x2 - r2) ^ (x1 - r1 > x2 + r2)) &&
                (y1 + r1 > y2 - r2) ^ (y1 - r1 > y2 + r2);
    }
}
