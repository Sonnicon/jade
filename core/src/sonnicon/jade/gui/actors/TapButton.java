package sonnicon.jade.gui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sonnicon.jade.gui.Gui;

public class TapButton extends Button {
    protected ClickListener tapClickListener;
    public Runnable tapAction;

    public TapButton(String style) {
        super(Gui.skin, style);
        clearListeners();

        tapClickListener = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (!isDisabled()) {
                    if (tapAction == null) {
                        tapped();
                    } else {
                        tapAction.run();
                    }

                }
            }
        };
        addListener(tapClickListener);
    }

    public TapButton(String style, Actor actor) {
        this(style);
        add(actor).grow();
    }

    public void setTapAction(Runnable action) {
        this.tapAction = action;
    }

    public void tapped() {

    }

    @Override
    public boolean isPressed() {
        return tapClickListener != null && tapClickListener.isVisualPressed();
    }

    @Override
    public boolean isOver() {
        return tapClickListener != null && tapClickListener.isOver();
    }

    @Override
    public ClickListener getClickListener() {
        return tapClickListener;
    }
}
