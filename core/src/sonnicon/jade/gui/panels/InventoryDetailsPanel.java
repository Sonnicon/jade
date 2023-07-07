package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.game.StorageSlotView;
import sonnicon.jade.graphics.Textures;
import sonnicon.jade.gui.Gui;

public class InventoryDetailsPanel extends Panel {
    protected StorageSlotView slot;

    protected Image imageEntity;
    protected Label labelEntity, labelDescription;

    protected Table tableContents;

    public InventoryDetailsPanel() {

    }

    @Override
    public void create() {
        super.create();
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

    @Override
    protected void recreate() {
        Drawable image;
        String title, description;
        StorableComponent storableComponent = slot.getStack().entity.getComponent(StorableComponent.class);
        if (storableComponent != null) {
            image = storableComponent.icons[0];
            title = storableComponent.displayName;
            description = storableComponent.displayDescription;
        } else {
            image = Textures.atlasFindDrawable("icon-error");
            title = slot.getStack().entity.toString();
            description = "Missing Description...";
        }
        imageEntity.setDrawable(image);
        labelEntity.setText(title);
        labelDescription.setText(description);
    }

    public void show(StorageSlotView slot) {
        if (slot != null && slot.hasStack()) {
            this.slot = slot;
            show();
        }
    }
}
