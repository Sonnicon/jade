package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.gui.panels.InventoryPanel;

public class StageIngame extends Stage {
    protected Table tableMain;
    protected Table tableLeft;
    protected Table tableRight;
    public InventoryPanel panelInventory;

    public StageIngame() {
        super(new ScreenViewport());
        create();
        Gamestate.State.ingame.stage = this;
        ((InputMultiplexer)Gamestate.State.ingame.inputProcessor).addProcessor(0, this);
    }

    protected void create() {
        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.align(Align.bottom);
        tableMain.debug();

        panelInventory = new InventoryPanel();

        tableLeft = new Table();
        tableLeft.add(new InventoryHandButton(0));

        tableRight = new Table();
        tableRight.add(new InventoryHandButton(1));

        tableMain.add(tableLeft).expandX().left();
        tableMain.add(tableRight).expandX().right();


        addActor(tableMain);

    }

    public void resize() {
        panelInventory.resize();
    }
}
