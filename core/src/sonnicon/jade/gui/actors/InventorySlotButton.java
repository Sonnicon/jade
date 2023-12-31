package sonnicon.jade.gui.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import sonnicon.jade.entity.components.storage.StorableComponent;
import sonnicon.jade.game.EntityStorageSlot;
import sonnicon.jade.game.Gamestate.State;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.gui.StageIngame;

import java.util.ArrayDeque;
import java.util.LinkedList;

public class InventorySlotButton extends TapButton {
    public EntityStorageSlot slot;
    public LinkedList<Actor> associatedActors = new LinkedList<>();

    protected Stack entityStack;
    protected Table iconTable;

    protected Cell<Stack> cellEntityStack;

    public static EntityStorageSlot selectedStorageSlot;
    public static ArrayDeque<InventorySlotButton> checkedButtons = new ArrayDeque<>();
    public static ArrayDeque<InventorySlotButton> flaggedButtons = new ArrayDeque<>();

    private static Vector2 tempVec = new Vector2(0, 0);

    public InventorySlotButton() {
        super("button-inventory-slot");
    }

    public InventorySlotButton(EntityStorageSlot slot) {
        this();
        this.slot = slot;
        create();
    }

    public void create() {
        entityStack = new Stack();
        iconTable = new Table();
        iconTable.setFillParent(true);
        recreate();
    }

    public void recreate() {
        if (slot == null || !slot.exists()) {
            ((StageIngame) State.ingame.getStage()).panelInventory.removeInventoryButton(slot);
            return;
        }

        clearChildren();
        addActor(iconTable);
        if (!slot.isEmpty()) {
            if (slot.isSelected()) {
                setChecked(true);
                if (!checkedButtons.contains(this)) {
                    checkedButtons.add(this);
                }
            }

            StorableComponent comp = slot.getEntity().getComponent(StorableComponent.class);
            entityStack.clear();
            if (comp == null) return;
            entityStack.addActor(new Image(comp.icons[0]));
            if (slot.getAmount() > 1) {
                entityStack.addActor(new Label(String.valueOf(slot.getAmount()), Gui.skin));
            }
            cellEntityStack = add(entityStack).fill().size(96f, 96f);
        } else {
            removeAssociatedActors();
        }
    }

    public void setIcon(Actor actor) {
        iconTable.clearChildren();
        iconTable.add(actor);
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
            ((StageIngame) State.ingame.getStage()).panelInventoryDetails.show(slot);
            unselectAll();
        } else if (selectedStorageSlot.isEmpty() ^ slot.isEmpty()) {
            // One click: empty
            // One click: has stack
            EntityStorageSlot slotFrom = slot.isEmpty() ? selectedStorageSlot : slot;
            EntityStorageSlot slotTo = slot.isEmpty() ? slot : selectedStorageSlot;
            slotFrom.moveAll(slotTo, null);
            updateChangedSlots();
            ((StageIngame) State.ingame.getStage()).panelInventory.recreateContainerGroup();
        } else if (!slot.isEmpty()) {
            // Both click: has stack
            // We try to move one way, then the other
            if (!tappedTryMove(selectedStorageSlot, slot)) {
                tappedTryMove(slot, selectedStorageSlot);
            }
        }
    }

    protected boolean tappedTryMove(EntityStorageSlot source, EntityStorageSlot destination) {
        boolean isMatch = destination.matchesEntity(source);
        boolean isStore = destination.hasStorageEntity();
        if (isMatch && isStore) {
            tempVec = localToStageCoordinates(tempVec.set(0f, 0f));

            ((StageIngame) State.ingame.getStage()).popupInventoryMove.show(tempVec.x + 48f, tempVec.y - 48f, action -> {
                source.moveAll(destination, action);
                if (action == EntityStorageSlot.InventoryMove.insert && destination.getAmount() > 1) {
                    ((StageIngame) State.ingame.getStage()).panelInventory.appendNewSlots();
                }

                updateChangedSlots();
            });
            return true;
        }

        boolean result = false;
        if (isMatch) {
            result = source.moveAll(destination) >= 0;
        } else if (isStore) {
            result = source.moveAll(destination, EntityStorageSlot.InventoryMove.insert) >= 0;
            if (destination.getAmount() > 1) {
                ((StageIngame) State.ingame.getStage()).panelInventory.appendNewSlots();
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
        checkedButtons.forEach(InventorySlotButton::flagRecreate);
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
        flaggedButtons.clear();
    }

    public void flagRecreate() {
        if (!flaggedButtons.contains(this)) {
            flaggedButtons.add(this);
        }
    }

    public boolean removeButton() {
        removeAssociatedActors();
        return remove();
    }

    public void removeAssociatedActors() {
        associatedActors.forEach(Actor::remove);
        associatedActors.clear();
    }

    @Override
    public float getPrefWidth() {
        return 116f;
    }

    @Override
    public float getPrefHeight() {
        return 116f;
    }
}
