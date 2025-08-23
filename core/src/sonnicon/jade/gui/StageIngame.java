package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.Jade;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.entity.components.storage.CharacterStorageComponent;
import sonnicon.jade.entity.components.storage.StorageComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.graphics.RenderLayer;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.graphics.draw.SpriteBatch;
import sonnicon.jade.graphics.particles.TextParticle;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.gui.actors.TapButton;
import sonnicon.jade.gui.panels.DebugPanel;
import sonnicon.jade.gui.panels.InventoryDetailsPanel;
import sonnicon.jade.gui.panels.InventoryPanel;
import sonnicon.jade.gui.popups.InventoryMovePopup;

import java.util.LinkedList;

public class StageIngame extends GuiStage {
    // GUI elements
    protected Table tableMain;
    public InventoryPanel panelInventory;
    public InventoryDetailsPanel panelInventoryDetails;
    public DebugPanel panelDebug;

    public InventoryMovePopup popupInventoryMove;

    protected short nextHandIndex = 0;
    protected Table tableHandsLeft, tableHandsRight, tableToolbar;
    protected HorizontalGroup groupToolbar;

    protected LinkedList<Table> entriesToolbar;

    public StageIngame() {
        super(new ScreenViewport(), (SpriteBatch) RenderLayer.gui.batch);
//        super(new ScreenViewport());
        ((InputMultiplexer) Gamestate.State.ingame.inputProcessor).addProcessor(0, this);

        panelInventory = new InventoryPanel();
        panelInventoryDetails = new InventoryDetailsPanel();
        popupInventoryMove = new InventoryMovePopup();
        panelDebug = new DebugPanel();
    }

    @Override
    public void setup() {
        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.align(Align.bottom);
        addActor(tableMain);

        // Hands
        tableHandsLeft = new Table();
        tableHandsRight = new Table();

        // Toolbar
        entriesToolbar = new LinkedList<>();
        addToolbarButton("icon-arrow-right", () -> {
            Jade.renderer.particles.createParticle(TextParticle.class, 100, 100).setText("tick");
            //todo
            Clock.tick(1f);
        });

        addToolbarButton("icon-arrow-right-double", () -> {
            Jade.renderer.particles.createParticle(TextParticle.class, 100, 100).setText("tickFast");
            //todo
            Clock.tickFast(1f);
        });

        addToolbarButton("icon-arrow-right-double", () -> {
            Jade.renderer.particles.createParticle(TextParticle.class, 100, 100).setText("tickFast");
            //todo
            Clock.tickFast(1f);
        });

        groupToolbar = new HorizontalGroup();
        ScrollPane paneToolbar = new ScrollPane(groupToolbar);
        tableToolbar = new Table(Gui.skin);

        tableToolbar.add(paneToolbar).growX().pad(0f, 4f, 0f, 4f);
        tableToolbar.background("panel-toolbar-9p");

        groupToolbar.setFillParent(true);
        groupToolbar.left();
        groupToolbar.space(6f);

        paneToolbar.setScrollingDisabled(false, true);
        paneToolbar.setOverscroll(false, false);

        recreate();
    }

    public Table addToolbarButton(String drawable, Runnable clicked) {
        Table table = new Table();
        TapButton button = new TapButton("button-toolbar", new Image(Textures.atlasFindDrawable(drawable)));
        button.actorCell.pad(4f);

        button.setTapAction(clicked);
        table.add(button).size(76f).pad(3f, 0f, 3f, 0f);
        entriesToolbar.add(table);
        if (groupToolbar != null) {
            groupToolbar.addActor(table);
        }
        return table;
    }

    public void recreate() {
        tableMain.clearChildren();
        if (PlayerControlComponent.isControlled(null)) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) PlayerControlComponent.getEntity().getComponent(StorageComponent.class);
        if (storageComponent != null) {
            tableMain.add(tableHandsLeft).left().bottom();
            tableMain.add(tableToolbar).growX().align(Align.bottomLeft);
            tableMain.add(tableHandsRight).right().bottom();
            tableToolbar.setZIndex(tableHandsRight.getZIndex() + 1);

            entriesToolbar.forEach(actor -> groupToolbar.addActor(actor));

            recreateHands();
        }
    }

    public void recreateHands() {
        if (tableMain == null) {
            return;
        }

        tableHandsLeft.clearChildren();
        tableHandsRight.clearChildren();
        if (PlayerControlComponent.isControlled(null)) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) PlayerControlComponent.getEntity().getComponent(StorageComponent.class);
        if (storageComponent != null) {
            nextHandIndex = 0;
            for (EntityStorageSlot hand : storageComponent.hands) {
                addHand(hand);
            }
        }

        if (panelInventory.isCreated() && panelInventory.isVisible()) {
            panelInventory.recreate();
        }
    }

    public void addHand(EntityStorageSlot slot) {
        InventoryHandButton handButton = new InventoryHandButton(slot, nextHandIndex);
        (nextHandIndex % 2 == 0 ? tableHandsLeft : tableHandsRight).add(handButton).row();
        nextHandIndex++;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        panelInventory.resize();
    }
}
