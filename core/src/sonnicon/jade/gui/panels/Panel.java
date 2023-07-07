package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sonnicon.jade.gui.Gui;

public abstract class Panel extends Table {
    protected Table wrapper;
    protected Cell<Panel> cell;

    public Panel() {
        super(Gui.skin);
        create();
    }

    public void create() {
        wrapper = new Table(Gui.skin);
        wrapper.setFillParent(true);
        cell = wrapper.add(this).grow();
    }

    protected void recreate() {

    }

    public void show() {
        recreate();
        Gui.activeStage.addActor(wrapper);
        wrapper.setVisible(true);
    }

    public void hide() {
        if (wrapper.hasParent()) {
            wrapper.getParent().removeActor(wrapper);
        }
    }
}
