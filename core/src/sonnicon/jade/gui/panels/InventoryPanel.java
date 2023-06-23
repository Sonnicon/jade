package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import sonnicon.jade.entity.components.StorableComponent;
import sonnicon.jade.entity.components.StorageComponent;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.gui.actors.InventorySlotButton;
import sonnicon.jade.util.DoubleLinkedList;

import java.util.Stack;

public class InventoryPanel extends Panel {
    protected Table entriesTable;
    protected Cell<ScrollPane> entriesCell;

    protected Table containerTable;
    protected ImageButton containerExitButton;
    public Stack<EntityStorage> containerStack = new Stack<>();

    public InventorySlotButton selectedInventoryButton;

    @Override
    public void create() {
        wrapper.pad(4f, 100f, 0f, 100f);
        wrapper.debugAll();

        background("button-inventory-1-9p");

        Table tableTitle = new Table();
        add(tableTitle).growX().padLeft(4f).row();

        Label labelTitle = new Label("Contents", Gui.skin);
        tableTitle.add(labelTitle).left().expandX();

        ImageButton buttonClose = new ImageButton(Gui.skin, "imagebutton-inventorycontent-close");
        buttonClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
        tableTitle.add(buttonClose);

        entriesTable = new Table(Gui.skin);
        ScrollPane entriesPane = new ScrollPane(entriesTable);

        entriesPane.setScrollingDisabled(true, false);
        entriesCell = add(entriesPane).grow().pad(2f);
        entriesTable.align(Align.topLeft);

        Table containersWrapper = new Table(Gui.skin);
        containersWrapper.background("button-inventory-1-9p");
        containerTable = new Table(Gui.skin);
        containerTable.defaults().left().pad(0f, 4f, 0f, 4f).size(64f);
        containerTable.left();
        ScrollPane containersPane = new ScrollPane(containerTable);
        wrapper.row();
        wrapper.add(containersWrapper).growX().pad(4f, 0f, 0f, 0f).height(96f);
        containersWrapper.add(containersPane).growX().pad(0f, -4f, 0f, -4f);
        containersPane.setScrollingDisabled(false, true);

        containerExitButton = new ImageButton(Gui.skin, "imagebutton-inventorycontent-close");
        containerExitButton.background("button-inventory-2-9p");
        containerExitButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                containerStack.pop();
                if (containerStack.empty()) {
                    hide();
                } else {
                    createContents();
                }
            }
        });
    }

    protected void createContents() {
        containerTable.clearChildren();
        containerTable.add(containerExitButton);

        entriesTable.clearChildren();
        float sumWidth = 0;
        EntityStorage storage = containerStack.peek();

        DoubleLinkedList.DoubleLinkedListNodeIterator<EntityStorage.EntityStack> iter = storage.stacks.nodeIterator();
        while (iter.hasNext()) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node = iter.next();
            EntityStorage.EntityStack stack = node.value;

            InventorySlotButton slot = new InventorySlotButton(node);
            float slotPrefWidth = slot.getPrefWidth();
            sumWidth += slotPrefWidth;
            if (sumWidth >= entriesCell.getActorWidth()) {
                entriesTable.row();
                sumWidth = slotPrefWidth;
            }
            entriesTable.add(slot).align(Align.topLeft);

            if (stack == null) {
                continue;
            }

            StorageComponent stackStorage = stack.entity.getComponent(StorageComponent.class);
            if (stackStorage != null) {
                StorableComponent comp = stack.entity.getComponent(StorableComponent.class);
                if (comp == null) continue;
                Button containerButton = new Button(Gui.skin, "button-inventorycontent");
                containerButton.add(new Image(comp.icons[0])).grow();
                containerButton.addCaptureListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        containerStack.push(stackStorage.storage);
                        createContents();
                    }
                });
                containerTable.add(containerButton).size(64f);
            }
        }
    }

    public void show(EntityStorage storage) {
        containerStack.clear();
        containerStack.push(storage);
        createContents();
        show();
    }

    public void resize() {
        if (wrapper.hasParent()) {
            createContents();
        }
    }
}
