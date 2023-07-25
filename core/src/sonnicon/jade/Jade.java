package sonnicon.jade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.Content;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.input.Input;

public class Jade extends ApplicationAdapter {
    public static Renderer renderer;

    @Override
    public void create() {
        Textures.init();
        renderer = new Renderer();
        Input.init();
        Gui.init();
        Content.init();


        Gamestate.setState(Gamestate.State.menu);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Clock.update(delta);
        Jade.renderer.render(delta);
        Gui.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Jade.renderer.resize(width, height);
        Gui.resize(width, height);
    }

    @Override
    public void dispose() {

    }
}
