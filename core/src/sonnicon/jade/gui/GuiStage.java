package sonnicon.jade.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class GuiStage extends Stage {

    public GuiStage() {
        super();
    }

    public GuiStage(Viewport viewport) {
        super(viewport);
    }

    public GuiStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    public void create() {

    }
}
