package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import sonnicon.jade.gui.Gui;

public class InventorySlotButton extends Button {

    public InventorySlotButton() {
        super(Gui.skin);
        setTouchable(Touchable.enabled);
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
