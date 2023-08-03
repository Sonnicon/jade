package sonnicon.jade.graphics.draw;

import com.badlogic.gdx.math.Matrix4;

public interface GraphicsBatch {
    void flush();

    default void begin() {

    }

    default void end() {
        flush();
    }

    void setProjectionMatrix(Matrix4 projection);

    void setTransformMatrix(Matrix4 transform);
}