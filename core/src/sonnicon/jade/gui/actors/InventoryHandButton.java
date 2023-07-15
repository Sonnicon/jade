package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.gui.Gui;

public class InventoryHandButton extends InventorySlotButton {
    public int handNumber;

    protected Table numberTable;

    public InventoryHandButton(EntityStorageSlot slot, int handNumber) {
        super();
        this.slot = slot;
        this.handNumber = handNumber;
        create();

        slot.registerEvent(null, ignored -> recreate());
    }

    @Override
    public void create() {
        numberTable = new Table();
        numberTable.add(new Label(String.valueOf(handNumber), Gui.skin));
        numberTable.setFillParent(true);
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));
        super.create();
    }

    @Override
    public void recreate() {
        if (slot == null || !slot.exists()) {
            Gui.stageIngame.panelInventory.removeInventoryButton(slot);
            return;
        }

        clearChildren();
        super.recreate();
        addActorAt(0, numberTable);
    }

    @Override
    public void tapped() {
        //todo
    }
}