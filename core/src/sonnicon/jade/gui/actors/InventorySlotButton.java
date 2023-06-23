package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.DoubleLinkedList;

public class InventorySlotButton extends TapButton {
    public DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> storageNode;

    public InventorySlotButton() {
        super("button-inventorycontent");
    }

    public InventorySlotButton(DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> storageNode) {
        this();
        create(storageNode);
    }

    public void create(DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> storageNode) {
        this.storageNode = storageNode;
        if (this.storageNode == null || this.storageNode.value == null) {
            return;
        }

        EntityStorage.EntityStack stack = this.storageNode.value;

        Stack st = new Stack();
        add(st).grow();

        StorableComponent comp = stack.entity.getComponent(StorableComponent.class);
        if (comp == null) return;
        st.addActor(new Image(comp.icons[0]));
        if (stack.amount > 1) {
            st.addActor(new Label(String.valueOf(stack.amount), Gui.skin));
        }
    }

    @Override
    public void tapped() {
        if (storageNode == null || storageNode.value == null) {
            return;
        }
        Gui.stageIngame.panelInventoryDetails.show(storageNode);
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
