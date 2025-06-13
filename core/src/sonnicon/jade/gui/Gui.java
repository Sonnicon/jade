package sonnicon.jade.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.Textures;

public class Gui {
    public static Skin skin;
    private static BitmapFont font;

    public static void init() {
        skin = new SkinJade(Gdx.files.internal("skin.json"), Textures.atlas);
        font = skin.getFont("foldit.fnt");
    }

    public static void resize(int width, int height) {
        Gamestate.getStage().resize(width, height);
    }

    public static BitmapFont getFont() {
        return font;
    }
}
