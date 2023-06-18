package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.gui.actors.InventorySlotButton;

public class InventoryPanel extends Panel {
    protected Table entriesTable;
    protected Cell<ScrollPane> entriesCell;

    public EntityStorage currentStorage;

    @Override
    public void create() {
        background("button-inventory-1-9p");

        Table tableTitle = new Table();
        add(tableTitle).growX().top().row();

        Label labelTitle = new Label("Contents", Gui.skin);
        tableTitle.add(labelTitle).left().expandX();

        ImageButton buttonClose = new ImageButton(Textures.atlasFindDrawable("icon-cross"));
        buttonClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
        tableTitle.add(buttonClose).right();


        entriesTable = new Table();
        ScrollPane entriesPane = new ScrollPane(entriesTable);;
        entriesPane.setScrollingDisabled(true, false);
        entriesCell = add(entriesPane).grow().pad(2f);
        entriesTable.align(Align.topLeft);

        wrapper.pad(4f, 4f, 100f, 4f);
    }

    protected void createContents() {
        entriesTable.clearChildren();
        float sumWidth = 0;
        for (EntityStorage.EntityStack stack : currentStorage.stacks) {
            InventorySlotButton slot = new InventorySlotButton(stack);
            float slotPrefWidth = slot.getPrefWidth();
            sumWidth += slotPrefWidth;
            if (sumWidth >= entriesCell.getActorWidth()) {
                entriesTable.row();
                sumWidth = slotPrefWidth;
            }
            entriesTable.add(slot).align(Align.topLeft);
        }
    }

    public void show(EntityStorage storage) {
        this.currentStorage = storage;
        createContents();
        show();
    }

    public void resize() {
        if (wrapper.hasParent()) {
            createContents();
        }
    }
}
