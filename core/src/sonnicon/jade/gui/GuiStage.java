package sonnicon.jade.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import sonnicon.jade.graphics.IRenderable;
import sonnicon.jade.graphics.Renderer;
import sonnicon.jade.graphics.draw.GraphicsBatch;
import sonnicon.jade.graphics.draw.SpriteBatch;

public abstract class GuiStage extends Stage implements IRenderable {
    protected boolean created = false;

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
        getBatch().setProjectionMatrix(getCamera().combined);
    }

    public void create() {
        if (!created) {
            setup();
        }
        created = true;
    }

    protected abstract void setup();

    @Override
    public void render(GraphicsBatch batch, float delta, Renderer.RenderLayer layer) {
        getRoot().draw((SpriteBatch) batch, 1.0f);
    }

    @Override
    public boolean culled(Renderer.RenderLayer layer) {
        return !getRoot().isVisible();
    }
}
