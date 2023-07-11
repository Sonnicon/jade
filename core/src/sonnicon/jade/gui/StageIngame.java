package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.entity.Entity;
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

        recreate();
    }

    public void recreate() {
        tableMain.clearChildren();

        StorageComponent storageComponent = controlledEntity.getComponent(StorageComponent.class);
        if (storageComponent != null) {
            Table leftTable = new Table();
            Table rightTable = new Table();

            tableMain.add(leftTable).width(96f).left().bottom();
            tableMain.add(new Table()).growX();
            tableMain.add(rightTable).width(96f).right().bottom();

            int hand = 0;
            int index = 0;
            for (EntityStorageSlot slot : storageComponent.storage.slots) {
                //todo hands
                if (index < 2) {
                    InventoryHandButton handButton = new InventoryHandButton(slot, hand);
                    if (hand++ % 2 == 0) {
                        leftTable.add(handButton).row();
                    } else {
                        rightTable.add(handButton).row();
                    }
                }
                index++;
            }
        }
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
