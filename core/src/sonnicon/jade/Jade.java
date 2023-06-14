package sonnicon.jade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.input.Input;
import sonnicon.jade.world.Chunk;
import sonnicon.jade.world.World;

public class Jade extends ApplicationAdapter {

    @Override
    public void create() {
        Textures.init();
        Renderer.init();
        Gui.init();

        World w = new World();
        for (int i = 0; i < 16; i++) {
            new Chunk((short) (i / 4), (short) (i % 4), w);
        }

        Input.init();

        Gamestate.setState(Gamestate.State.menu);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Renderer.render(delta);
        Gui.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Renderer.resize(width, height);
        Gui.resize(width, height);
    }

    @Override
    public void dispose() {

    }
}
