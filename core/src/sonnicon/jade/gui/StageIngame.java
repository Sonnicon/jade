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
import sonnicon.jade.graphics.Renderer;
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

    protected int nextHandIndex = 0;
    protected Table handTableLeft, handTableRight, toolbarWrapper;
    protected HorizontalGroup toolbarGroup;

    protected LinkedList<Table> toolbarEntries;

    public StageIngame() {
        super(new ScreenViewport(), (SpriteBatch) Renderer.Batch.gui.batch);
        ((InputMultiplexer) Gamestate.State.ingame.inputProcessor).addProcessor(0, this);

        panelInventory = new InventoryPanel();
        panelInventoryDetails = new InventoryDetailsPanel();
        popupInventoryMove = new InventoryMovePopup();
        panelDebug = new DebugPanel();
    }

    @Override
    public void setup() {
        Jade.renderer.addRenderable(this, Renderer.RenderLayer.gui);

        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.align(Align.bottom);
        addActor(tableMain);

        handTableLeft = new Table();
        handTableRight = new Table();

        toolbarEntries = new LinkedList<>();
        addToolbarButton("icon-arrow-right", () -> {
            Jade.renderer.particles.createParticle(TextParticle.class, 100, 100).setText("tick");
            //todo
            Clock.tick(1f);
        });


        // toolbarWrapper { toolbarPane { toolbarGroup } } }
        toolbarGroup = new HorizontalGroup();
        ScrollPane toolbarPane = new ScrollPane(toolbarGroup);
        toolbarWrapper = new Table(Gui.skin);

        toolbarWrapper.add(toolbarPane).growX();
        toolbarWrapper.background("panel-toolbar-9p");

        toolbarGroup.setFillParent(true);
        toolbarGroup.left();
        toolbarGroup.space(8f);

        toolbarPane.setScrollingDisabled(false, true);
        toolbarPane.setOverscroll(false, false);

        recreate();
    }

    public Table addToolbarButton(String drawable, Runnable clicked) {
        Table table = new Table();
        TapButton button = new TapButton("panel-1", new Image(Textures.atlasFindDrawable(drawable)));
        button.setTapAction(clicked);
        table.add(button).size(56f);
        toolbarEntries.add(table);
        if (toolbarGroup != null) {
            toolbarGroup.addActor(table);
        }
        return table;
    }

    public void recreate() {
        tableMain.clearChildren();
        if (PlayerControlComponent.isControlled(null)) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) PlayerControlComponent.getControlledEntity().getComponent(StorageComponent.class);
        if (storageComponent != null) {
            tableMain.add(handTableLeft).width(96f).left().bottom();
            tableMain.add(toolbarWrapper).growX().align(Align.bottomLeft).pad(0f, -4f, 0f, -4f).height(80f);
            tableMain.add(handTableRight).width(96f).right().bottom();
            toolbarWrapper.setZIndex(handTableRight.getZIndex() + 1);

            toolbarEntries.forEach(actor -> toolbarGroup.addActor(actor));

            recreateHands();
        }
    }

    public void recreateHands() {
        if (tableMain == null) {
            return;
        }

        handTableLeft.clearChildren();
        handTableRight.clearChildren();
        if (PlayerControlComponent.isControlled(null)) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) PlayerControlComponent.getControlledEntity().getComponent(StorageComponent.class);
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
        (nextHandIndex % 2 == 0 ? handTableLeft : handTableRight).add(handButton).row();
        nextHandIndex++;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        panelInventory.resize();
    }
}
