package sonnicon.jade.gui.popups;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import sonnicon.jade.game.StorageSlotView;
import sonnicon.jade.gui.actors.TapButton;

import java.util.function.Consumer;

public class InventoryMovePopup extends Popup {
    protected Consumer<StorageSlotView.InventoryMove> resultConsumer;

    public InventoryMovePopup() {
        super();
        background("button-inventory-1-9p");

        int displayedButtons = 0;
        for (StorageSlotView.InventoryMove action : StorageSlotView.InventoryMove.values()) {
            if (action.icon == null) {
                continue;
            }

            TapButton button = new TapButton("button-inventorycontent");
            button.add(new Image(action.icon)).grow();
            button.tapAction = () -> hide(action);
            add(button).size(64f);

            displayedButtons++;
        }

        setSize(displayedButtons * 64f + 32f, 96f);
    }

    public void show(float x, float y, Consumer<StorageSlotView.InventoryMove> consumer) {
        resultConsumer = consumer;
        show(x, y);
    }

    public void hide(StorageSlotView.InventoryMove result) {
        resultConsumer.accept(result);
        super.hide();
    }

    @Override
    public void hide() {
        resultConsumer.accept(StorageSlotView.InventoryMove.cancel);
        super.hide();
    }
}
