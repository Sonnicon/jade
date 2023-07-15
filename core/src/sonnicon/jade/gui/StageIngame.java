package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.CharacterStorageComponent;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.gui.panels.InventoryDetailsPanel;
import sonnicon.jade.gui.panels.InventoryPanel;
import sonnicon.jade.gui.popups.InventoryMovePopup;

public class StageIngame extends Stage {
    // Entity that this player is controlling
    protected Entity controlledEntity;

    // GUI elements
    protected Table tableMain;
    public InventoryPanel panelInventory;
    public InventoryDetailsPanel panelInventoryDetails;

    public InventoryMovePopup popupInventoryMove;

    protected int nextHandIndex = 0;
    protected Table handTableLeft, handTableRight;

    public StageIngame() {
        super(new ScreenViewport());
        Gamestate.State.ingame.stage = this;
        ((InputMultiplexer) Gamestate.State.ingame.inputProcessor).addProcessor(0, this);

        panelInventory = new InventoryPanel();
        panelInventoryDetails = new InventoryDetailsPanel();

        popupInventoryMove = new InventoryMovePopup();
    }

    public void create() {
        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.align(Align.bottom);
        tableMain.debug();
        addActor(tableMain);

        handTableLeft = new Table();
        handTableRight = new Table();

        recreate();
    }

    public void recreate() {
        tableMain.clearChildren();
        if (controlledEntity == null) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) controlledEntity.getComponent(StorageComponent.class);
        if (storageComponent != null) {
            tableMain.add(handTableLeft).width(96f).left().bottom();
            tableMain.add(new Table()).growX();
            tableMain.add(handTableRight).width(96f).right().bottom();

            recreateHands();
        }
    }

    public void recreateHands() {
        if (tableMain == null) {
            return;
        }

        handTableLeft.clearChildren();
        handTableRight.clearChildren();
        if (controlledEntity == null) {
            return;
        }

        CharacterStorageComponent storageComponent = (CharacterStorageComponent) controlledEntity.getComponent(StorageComponent.class);
        if (storageComponent != null) {
            nextHandIndex = 0;
            for (EntityStorageSlot hand : storageComponent.hands) {
                addHand(hand);
            }
        }
    }

    public void addHand(EntityStorageSlot slot) {
        InventoryHandButton handButton = new InventoryHandButton(slot, nextHandIndex);
        (nextHandIndex % 2 == 0 ? handTableLeft : handTableRight).add(handButton).row();
        nextHandIndex++;
    }

    public void resize() {
        panelInventory.resize();
    }

    public void setControlledEntity(Entity entity) {
        this.controlledEntity = entity;
        create();
        panelInventory.hide();
    }

    public Entity getControlledEntity() {
        return this.controlledEntity;
    }
}
