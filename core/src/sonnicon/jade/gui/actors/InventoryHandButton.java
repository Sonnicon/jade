package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.gui.Gui;

public class InventoryHandButton extends InventorySlotButton {
    public int handNumber;

    public InventoryHandButton(EntityStorageSlot slot, int handNumber) {
        super(slot);
        this.handNumber = handNumber;
    }

    @Override
    public void create() {
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));
        add(new Label(String.valueOf(handNumber), Gui.skin));

        super.create();
    }
}