package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.DoubleLinkedList;

public class InventoryHandButton extends InventorySlotButton {
    public int handNumber;

    public InventoryHandButton(DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> storageNode, int handNumber) {
        super(storageNode);
        this.handNumber = handNumber;
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));
        add(new Label(String.valueOf(handNumber), Gui.skin));
    }
}