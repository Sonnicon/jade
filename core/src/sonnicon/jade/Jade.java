package sonnicon.jade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.input.Input;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

public class Jade extends ApplicationAdapter {

    @Override
    public void create() {
        Textures.init();
        Renderer.init();

        World w = new World();
        for (int i = 0; i < 16; i++) {
            new Chunk((short) (i / 4), (short) (i % 4), w);
        }
        Gdx.input.setInputProcessor(new Input());
    }

    @Override
    public void render() {
        Renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        Renderer.resize(width, height);
    }

    @Override
    public void dispose() {

    }
}
