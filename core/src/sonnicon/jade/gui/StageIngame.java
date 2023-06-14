package sonnicon.jade.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.actors.HandButton;

import java.util.HashSet;

public class StageIngame extends Stage {
    protected Table mainTable;

    public StageIngame() {
        super(new ScreenViewport());
        create();
        Gamestate.State.ingame.stage = this;
    }

    protected void create() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.debug();

        mainTable.add(new HandButton(0));




        addActor(mainTable);
    }
}
