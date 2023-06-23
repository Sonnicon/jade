package sonnicon.jade.gui;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sonnicon.jade.entity.Entity;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.game.LimitedEntityStorage;
import sonnicon.jade.gui.actors.InventoryHandButton;
import sonnicon.jade.gui.panels.InventoryDetailsPanel;
import sonnicon.jade.gui.panels.InventoryPanel;
import sonnicon.jade.util.DoubleLinkedList;

public class StageIngame extends Stage {
    protected Entity controlledEntity;

    protected Table tableMain;
    public InventoryPanel panelInventory;
    public InventoryDetailsPanel panelInventoryDetails;

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
        panelInventoryDetails = new InventoryDetailsPanel();
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
            DoubleLinkedList.DoubleLinkedListNodeIterator<EntityStorage.EntityStack> iter = storage.stacks.nodeIterator();
            int index = 0;
            while (iter.hasNext()) {
                DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node = iter.next();
                if (storage.slots.get(index).type == LimitedEntityStorage.SlotType.hand) {
                    InventoryHandButton handButton = new InventoryHandButton(node, hand);
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
