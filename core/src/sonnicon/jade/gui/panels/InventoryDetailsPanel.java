package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.DoubleLinkedList;

public class InventoryDetailsPanel extends Panel {
    protected DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> slot;

    protected Image imageEntity;
    protected Label labelEntity, labelDescription;

    protected Table tableContents;

    @Override
    public void create() {
        wrapper.pad(32f, 32f, 32f, 32f);
        wrapper.debugAll();
        cell.maxSize(600f, 1000f);

        background("button-inventory-1-9p");

        Table tableTitle = new Table();
        add(tableTitle).growX().padLeft(4f).top();

        imageEntity = new Image();
        tableTitle.add(imageEntity).size(64f).left().pad(4f);

        labelEntity = new Label("", Gui.skin);
        labelEntity.setFontScale(1.2f);
        tableTitle.add(labelEntity).left().expandX().padLeft(12f);

        ImageButton buttonClose = new ImageButton(Gui.skin, "imagebutton-inventorycontent-close");
        buttonClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
        tableTitle.add(buttonClose);

        add(buttonClose).top().right().row();

        tableContents = new Table();
        add(tableContents).grow().colspan(2);

        Table tableDescripton = new Table(Gui.skin);
        tableDescripton.background("button-inventory-2-9p");
        ScrollPane paneDescription = new ScrollPane(tableDescripton);
        paneDescription.setScrollingDisabled(true, false);
        tableContents.add(paneDescription).growX();
        tableDescripton.add(labelDescription = new Label("", Gui.skin));
    }

    protected void createContents() {
        if (slot == null) {
            hide();
            return;
        }

        Drawable image;
        String title, description;
        StorableComponent storableComponent = slot.value.entity.getComponent(StorableComponent.class);
        if (storableComponent != null) {
            image = storableComponent.icons[0];
            title = storableComponent.displayName;
            description = storableComponent.displayDescription;
        } else {
            image = Textures.atlasFindDrawable("icon-error");
            title = slot.value.toString();
            description = "Missing Description...";
        }
        imageEntity.setDrawable(image);
        labelEntity.setText(title);
        labelDescription.setText(description);
    }

    public void show(DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> slot) {
        this.slot = slot;
        createContents();
        show();
    }

    public void resize() {
        if (wrapper.hasParent()) {
            createContents();
        }
    }
}
