package sonnicon.jade.gui.popups;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import sonnicon.jade.gui.Gui;

public class TextPopup extends Popup {
    private static TextPopup defaultPopup;
    private Label label;

    @Override
    public void create() {
        super.create();
        background("panel-inventory-rounded-9p");
        label = new Label("", Gui.skin);
        add(label);
    }

    public void show(float x, float y, String text) {
        label.setText(text);
        label.invalidate();
        setSize(label.getPrefWidth() + 32f, label.getPrefHeight() + 16f);
        show(x, y);
    }

    public static void show(String text) {
        if (defaultPopup == null) {
            defaultPopup = new TextPopup();
            defaultPopup.create();
        }

        defaultPopup.show(100f, 100f, text);
    }
}
