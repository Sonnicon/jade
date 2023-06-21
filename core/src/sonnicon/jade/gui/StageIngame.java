package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.LimitedEntityStorage;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.gui.panels.InventoryPanel;

public class StageIngame extends Stage {
    protected Entity controlledEntity;

    protected Table tableMain;
    public InventoryPanel panelInventory;

    public StageIngame() {
        super(new ScreenViewport());
        Gamestate.State.ingame.stage = this;
        ((InputMultiplexer) Gamestate.State.ingame.inputProcessor).addProcessor(0, this);

        tableMain = new Table();
        tableMain.setFillParent(true);
        tableMain.align(Align.bottom);
        tableMain.debug();
        addActor(tableMain);

        panelInventory = new InventoryPanel();
    }

    protected void create() {
        tableMain.clearChildren();

        StorageComponent storageComponent = controlledEntity.getComponent(StorageComponent.class);
        if (storageComponent != null && storageComponent.storage instanceof LimitedEntityStorage) {
            Table leftTable = new Table();
            Table rightTable = new Table();

            tableMain.add(leftTable).width(96f).left().bottom();
            tableMain.add(new Table()).growX();
            tableMain.add(rightTable).width(96f).right().bottom();

            LimitedEntityStorage storage = (LimitedEntityStorage) storageComponent.storage;
            int hand = 0;
            for (int i = 0; i < storage.stacks.size(); i++) {
                if (storage.slots.get(i).type == LimitedEntityStorage.SlotType.hand) {
                    InventoryHandButton handButton = new InventoryHandButton(storage, hand, i);
                    if (hand++ % 2 == 0) {
                        leftTable.add(handButton).row();
                    } else {
                        rightTable.add(handButton).row();
                    }
                }
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
