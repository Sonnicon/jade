package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {
    void render(SpriteBatch batch);

    default boolean culled() {
        return false;
    }
}
