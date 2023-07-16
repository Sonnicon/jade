package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.StageIngame;
import sonnicon.jade.gui.panels.InventoryPanel;

public class InventoryContainerButton extends TapButton {
    public EntityStorageSlot slot;

    public InventoryContainerButton(EntityStorageSlot slot) {
        super("button-inventorycontent");
        this.slot = slot;
        create();
    }

    public void create() {
        pad(8f);
        recreate();
    }

    protected void recreate() {
        if (slot == null || !slot.exists()) {
            remove();
            return;
        }

        clearChildren();
        //todo find better way to make it identical
        StorableComponent storableComponent = slot.getEntity().getComponent(StorableComponent.class);
        add(new Image(storableComponent.icons[0])).grow();
    }

    @Override
    public void tapped() {
        StorageComponent storageComponent = slot.getEntity().getComponent(StorageComponent.class);
        InventoryPanel panelInventory = ((StageIngame) Gamestate.State.ingame.getStage()).panelInventory;
        panelInventory.containerStack.push(storageComponent.storage);
        panelInventory.recreate();

        if (slot == InventorySlotButton.selectedStorageSlot) {
            InventorySlotButton.unselectAll();
        }
    }

    @Override
    public float getPrefWidth() {
        return 64f;
    }

    @Override
    public float getPrefHeight() {
        return 64f;
    }
}
