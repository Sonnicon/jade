package sonnicon.jade.gui.popups;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sonnicon.jade.gui.Gui;

public abstract class Popup extends Table {
    protected WidgetGroup overlay;

    public Popup() {
        super(Gui.skin);
        create();
    }

    public void create() {
        overlay = new WidgetGroup();
        overlay.debug();

        overlay.setFillParent(true);

        // Hide if you click away
        overlay.setTouchable(Touchable.enabled);
        overlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        overlay.addActor(this);
    }

    public void show(float x, float y) {
        create();

        x = Math.max(0, Math.min(x, Gui.activeStage.getWidth() - getWidth()));
        y = Math.max(0, Math.min(y, Gui.activeStage.getHeight() - getHeight()));

        setPosition(x, y);

        Gui.activeStage.addActor(overlay);
        overlay.setVisible(true);
    }

    public void hide() {
        overlay.remove();
        overlay.setVisible(false);
    }
}
