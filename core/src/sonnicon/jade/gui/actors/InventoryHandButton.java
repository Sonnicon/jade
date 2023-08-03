package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.Gui;

public class InventoryHandButton extends InventorySlotButton {
    public int handNumber;
    private EventTypes.AnyEvent slotWatch;

    public InventoryHandButton(EntityStorageSlot slot, int handNumber) {
        super();
        this.slot = slot;
        this.handNumber = handNumber;
        create();
    }

    @Override
    public void create() {
        super.create();
        setIcon(new Label(String.valueOf(handNumber), Gui.skin));
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));
    }

    @Override
    public void tapped() {
        //todo
    }

    @Override
    protected void setParent(Group parent) {
        if (slotWatch != null) {
            slot.events.unregister(slotWatch);
        }
        slot.events.register(slotWatch = (type) -> recreate());
        super.setParent(parent);
    }

    @Override
    public boolean remove() {
        slot.events.unregister(slotWatch);
        slotWatch = null;
        return super.remove();
    }
}