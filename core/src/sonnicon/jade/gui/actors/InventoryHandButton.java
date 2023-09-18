package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.generated.EventTypes;
import sonnicon.jade.gui.Gui;

public class InventoryHandButton extends InventorySlotButton {
    public final short handNumber;
    private EventTypes.AnyEvent slotWatch;

    public static final short HAND_NONE = -1;

    public InventoryHandButton(EntityStorageSlot slot, short handNumber) {
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
    public void recreate() {
        super.recreate();
        if (cellEntityStack != null) {
            boolean left = handNumber % 2 == 0;
            cellEntityStack.pad(0f, left ? 9f : 0, 9f, left ? 0f : 9f);
        }
    }

    @Override
    public void tapped() {
        PlayerControlComponent control = PlayerControlComponent.getControlled();
        if (control.selectedHand == -1) {
            control.setSelectedHand(handNumber);
        } else {
            control.setSelectedHand(HAND_NONE);
        }
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

    @Override
    public float getPrefWidth() {
        return 114f;
    }

    @Override
    public float getPrefHeight() {
        return 114f;
    }
}