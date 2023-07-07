package sonnicon.jade.gui.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.StorageSlotView;
import sonnicon.jade.gui.Gui;

import java.util.ArrayDeque;

public class InventorySlotButton extends TapButton {
    public StorageSlotView slot;

    public static StorageSlotView selectedStorageSlot;
    public static ArrayDeque<InventorySlotButton> checkedButtons = new ArrayDeque<>();
    public static ArrayDeque<InventorySlotButton> flaggedButtons = new ArrayDeque<>();

    private static Vector2 tempVec = new Vector2(0, 0);

    public InventorySlotButton() {
        super("button-inventorycontent");
    }

    public InventorySlotButton(StorageSlotView slot) {
        this();
        this.slot = slot;
        create();
    }

    public void create() {
        if (slot == null || !slot.hasStack()) {
            return;
        }

        if (slot.isSelected()) {
            setChecked(true);
            if (!checkedButtons.contains(this)) {
                checkedButtons.add(this);
            }
        }

        EntityStorage.EntityStack stack = slot.getStack();
        Stack st = new Stack();
        add(st).grow();

        StorableComponent comp = slot.getEntity().getComponent(StorableComponent.class);
        if (comp == null) return;
        st.addActor(new Image(comp.icons[0]));
        if (stack.amount > 1) {
            st.addActor(new Label(String.valueOf(stack.amount), Gui.skin));
        }
    }

    public void recreate() {
        if (slot == null || !slot.exists()) {
            remove();
            return;
        }

        clearChildren();
        create();
    }

    @Override
    public void tapped() {
        if (slot == null) {
            return;
        }

        if (selectedStorageSlot == null) {
            // 1st click: any
            select();
            return;
        }

        if (slot.isSelected()) {
            // 2nd click: same slot
            Gui.stageIngame.panelInventoryDetails.show(slot);
            unselectAll();
        } else if (selectedStorageSlot.hasStack() ^ slot.hasStack()) {
            // One click: empty
            // One click: has stack
            StorageSlotView slotFrom = slot.hasStack() ? slot : selectedStorageSlot;
            StorageSlotView slotTo = slot.hasStack() ? selectedStorageSlot : slot;
            slotFrom.moveToSlot(slotTo);

            updateChangedSlots();
        } else if (selectedStorageSlot.hasStack()) {
            // Both click: has stack
            // We try to move one way, then the other
            if (!tappedTryMove(selectedStorageSlot, slot)) {
                tappedTryMove(slot, selectedStorageSlot);
            }
        }
    }

    protected boolean tappedTryMove(StorageSlotView source, StorageSlotView destination) {
        boolean isMatch = destination.matchesEntity(source);
        boolean isStore = destination.hasStorageEntity();
        if (isMatch && isStore) {
            tempVec = localToStageCoordinates(tempVec.set(0f, 0f));

            Gui.stageIngame.popupInventoryMove.show(tempVec.x + 48f, tempVec.y - 48f, action -> {
                source.moveTo(destination, action);
                if (action == StorageSlotView.InventoryMove.insert && destination.getStack().amount > 1) {
                    Gui.stageIngame.panelInventory.appendNewSlots();
                }

                updateChangedSlots();
            });
            return true;
        }

        boolean result = false;
        if (isMatch) {
            result = source.moveToSlot(destination) >= 0;
        } else if (isStore) {
            result = source.moveToStorage(destination) >= 0;
            if (destination.getStack().amount > 1) {
                Gui.stageIngame.panelInventory.appendNewSlots();
            }
        }
        // messy but needed to not unselect before popup finishes
        if (result) {
            updateChangedSlots();
        }
        return result;
    }

    private void updateChangedSlots() {
        flagRecreate();
        flagRecreateChecked();
        unselectAll();
        recreateFlagged();
    }

    public void select() {
        if (!slot.isSelected()) {
            setChecked(true);
            checkedButtons.add(this);
            selectedStorageSlot = slot;
        }
    }

    public static void unselectAll() {
        checkedButtons.forEach(button -> button.setChecked(false));
        checkedButtons.clear();
        selectedStorageSlot = null;
    }

    public static void recreateFlagged() {
        flaggedButtons.forEach(InventorySlotButton::recreate);
    }

    public void flagRecreate() {
        if (!flaggedButtons.contains(this)) {
            flaggedButtons.add(this);
        }
    }

    public static void flagRecreateChecked() {
        checkedButtons.forEach(button -> {
            if (!flaggedButtons.contains(button)) {
                flaggedButtons.add(button);
            }
        });
    }

    @Override
    public float getPrefWidth() {
        return 96f;
    }

    @Override
    public float getPrefHeight() {
        return 96f;
    }
}
