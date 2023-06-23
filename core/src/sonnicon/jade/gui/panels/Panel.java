package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import sonnicon.jade.gui.Gui;

public abstract class Panel extends Table {
    protected final Table wrapper;
    protected final Cell<Panel> cell;

    public Panel() {
        super(Gui.skin);

        wrapper = new Table(Gui.skin);
        wrapper.setFillParent(true);
        cell = wrapper.add(this).grow();

        create();
    }

    public abstract void create();

    public void show() {
        Gui.activeStage.addActor(wrapper);
        wrapper.setVisible(true);
    }

    public void hide() {
        if (wrapper.hasParent()) {
            wrapper.getParent().removeActor(wrapper);
        }
    }
}
