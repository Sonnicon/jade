package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sonnicon.jade.game.Gamestate;
import sonnicon.jade.gui.Gui;

public abstract class Panel extends Table {
    protected Table wrapper;
    protected Cell<Panel> cell;
    private boolean created = false;

    public Panel() {
        super(Gui.skin);
    }

    public void create() {
        wrapper = new Table(Gui.skin);
        wrapper.setFillParent(true);
        cell = wrapper.add(this).grow();
    }

    protected void recreate() {

    }

    public void show() {
        if (!created) {
            create();
            created = true;
        }
        recreate();
        Gamestate.getStage().addActor(wrapper);
        wrapper.setVisible(true);
    }

    public void hide() {
        if (wrapper.hasParent()) {
            wrapper.getParent().removeActor(wrapper);
        }
        wrapper.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return wrapper.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        wrapper.setVisible(visible);
    }

    public boolean isCreated() {
        return created;
    }
}
