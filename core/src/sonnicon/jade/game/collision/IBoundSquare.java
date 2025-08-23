package sonnicon.jade.game.collision;

public interface IBoundSquare extends IBoundRectangle {
    float getDiameter();

    default float getWidth() {
        return getDiameter();
    }

    default float getHeight() {
        return getDiameter();
    }
}
