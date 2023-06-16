package sonnicon.jade.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import sonnicon.jade.game.Gamestate;

public class StageMenuMain extends Stage {
    protected Table tableMain, tableLeftButtons;

    public StageMenuMain() {
        super(new ExtendViewport(400, 400));
        create();
        Gamestate.State.menu.stage = this;
    }

    protected void create() {
        tableMain = new Table(Gui.skin);
        tableMain.debug();
        tableMain.setFillParent(true);
        tableMain.defaults().pad(0f, 2f, 6f, 2f);

        Label labelTitle = new Label("hello world", Gui.skin);
        labelTitle.setFontScale(1.5f);
        tableMain.add(labelTitle).row();

        TextButton buttonPlay = new TextButton("Play", Gui.skin);

        buttonPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gamestate.setState(Gamestate.State.ingame);
            }
        });


        tableLeftButtons = new Table();
        tableLeftButtons.defaults().pad(4f).width(160f);
        tableLeftButtons.add(buttonPlay).row();
        tableLeftButtons.add(new TextButton("Settings", Gui.skin)).left().row();
        tableLeftButtons.add(new TextButton("About", Gui.skin)).left().row();

        tableMain.add(tableLeftButtons);

        addActor(tableMain);
    }
}
