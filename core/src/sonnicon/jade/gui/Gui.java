package sonnicon.jade.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.Textures;

public class Gui {
    protected static Stage stageMenu;
    protected static Stage stageIngame;
    public static Stage activeStage;

    public static Skin skin;

    public static void init() {
        skin = new SkinJade(Gdx.files.internal("skin.json"), Textures.atlas);

        stageMenu = new StageMenuMain();
        stageIngame = new StageIngame();
        Gamestate.State.menu.inputProcessor = stageMenu;

        setActiveStage(stageMenu);
    }

    public static void update() {
        if (activeStage != null) {
            activeStage.act();
        }
    }

    public static void render(float delta) {
        //todo update loop
        update();
        if (activeStage != null) {
            activeStage.draw();
        }
    }

    public static void resize(int width, int height) {
        activeStage.getViewport().update(width, height, true);
    }

    public static void setActiveStage(Stage stage) {
        activeStage = stage;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        activeStage.getViewport().apply();
    }
}
