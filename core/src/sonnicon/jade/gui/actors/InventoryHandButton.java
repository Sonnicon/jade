package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.gui.Gui;

public class InventoryHandButton extends InventorySlotButton {
    public int handNumber;

    public InventoryHandButton(EntityStorage storage, int handNumber, int slotIndex) {
        super(storage);
        this.handNumber = handNumber;
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));
        add(new Label(String.valueOf(handNumber), Gui.skin));
    }
}