package sonnicon.jade.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import sonnicon.jade.gui.Gui;

public class Gamestate {
    protected static State state;

    public static State getState() {
        return state;
    }

    public static void setState(State newState) {
        state = newState;
        Gui.setActiveStage(newState.stage);
        Gdx.input.setInputProcessor(newState.inputProcessor);
    }

    public enum State {
        menu,
        ingame;

        public Stage stage;
        public InputProcessor inputProcessor;
    }
}
