package sonnicon.jade.gui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.actors.InventoryHandButton;

public class StageIngame extends Stage {
    protected Table tableMain, tableBottom, tableTop, tableLeft, tableRight;

    public StageIngame() {
        super(new ScreenViewport());
        create();
        Gamestate.State.ingame.stage = this;
    }

    protected void create() {
        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.debug();

        tableLeft = new Table();
        tableLeft.add(new InventoryHandButton(0));

        tableRight = new Table();
        tableRight.add(new InventoryHandButton(1));

        tableMain.add(tableLeft).expandX().left();
        tableMain.add(tableRight).expandX().right();
        tableMain.align(Align.bottom);
        addActor(tableMain);
    }
}
