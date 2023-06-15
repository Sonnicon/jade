package sonnicon.jade.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IRenderable {
    void render(SpriteBatch batch, float delta);

    default boolean culled() {
        return false;
    }
}
