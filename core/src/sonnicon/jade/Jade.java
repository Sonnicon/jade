package sonnicon.jade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.Update;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.input.Input;

public class Jade extends ApplicationAdapter {

    @Override
    public void create() {
        Textures.init();
        Renderer.init();
        Input.init();
        Gui.init();
        Content.init();

        Gamestate.setState(Gamestate.State.menu);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Update.update(delta);
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
