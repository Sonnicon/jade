package sonnicon.jade.gui.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.game.InventorySlot;
import sonnicon.jade.gui.Gui;

public class HandButton extends Button {
    public int handNumber;
    public InventorySlot slot;

    public HandButton(int handNumber) {
        super(Gui.skin);
        this.handNumber = handNumber;
        setTouchable(Touchable.enabled);
        setStyle(Gui.skin.get("button-hand-" + handNumber % 2, ButtonStyle.class));

        add(new Label(String.valueOf(handNumber), Gui.skin));
    }
}
