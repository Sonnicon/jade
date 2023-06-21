package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.gui.Gui;

public class InventorySlotButton extends TapButton {
    public EntityStorage.EntityStack stack;
    public EntityStorage storage;

    public InventorySlotButton(EntityStorage storage) {
        super("button-inventorycontent");
        this.storage = storage;
    }

    public InventorySlotButton(EntityStorage storage, EntityStorage.EntityStack stack) {
        this(storage);
        create(stack);
    }

    public void create(EntityStorage.EntityStack stack) {
        this.stack = stack;
        if (this.stack == null) {
            return;
        }

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
