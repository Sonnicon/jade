package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import sonnicon.jade.game.EntityStorage;
import sonnicon.jade.game.StorageSlotView;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.gui.actors.InventoryContainerButton;
import sonnicon.jade.gui.actors.InventorySlotButton;
import sonnicon.jade.util.DoubleLinkedList;

import java.lang.reflect.Field;
import java.util.Stack;

public class InventoryPanel extends Panel {
    // Actors
    protected Table entriesTable;
    protected Cell<ScrollPane> entriesCell;
    protected HorizontalGroup containerGroup;
    protected ImageButton containerExitButton;

    // Entity storages in order we are in (for returning)
    public Stack<EntityStorage> containerStack = new Stack<>();

    protected StorageSlotView lastSlot;
    protected float sumWidth = 0;


    // can't do my own layouts easily without this
    protected static final Field fieldCellEndRow, fieldCellRow, fieldCellColumn, fieldTableRows, fieldTableColumns;

    static {
        try {
            fieldCellEndRow = Cell.class.getDeclaredField("endRow");
            fieldCellRow = Cell.class.getDeclaredField("row");
            fieldCellColumn = Cell.class.getDeclaredField("column");

            fieldTableRows = Table.class.getDeclaredField("rows");
            fieldTableColumns = Table.class.getDeclaredField("columns");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        fieldCellEndRow.setAccessible(true);
        fieldCellRow.setAccessible(true);
        fieldCellColumn.setAccessible(true);

        fieldTableRows.setAccessible(true);
        fieldTableColumns.setAccessible(true);
    }

    public InventoryPanel() {

    }

    @Override
    public void create() {
        super.create();
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

        entriesPane.setScrollingDisabled(false, false);
        entriesCell = add(entriesPane).grow().pad(2f);
        entriesTable.align(Align.topLeft);


        Table containersWrapper = new Table(Gui.skin);
        containersWrapper.background("button-inventory-1-9p");
        containerGroup = new HorizontalGroup();
        containerGroup.left().space(4f);
        ScrollPane containersPane = new ScrollPane(containerGroup);
        wrapper.row();
        wrapper.add(containersWrapper).growX().pad(4f, 0f, 0f, 0f).height(96f);
        containersWrapper.add(containersPane).growX().pad(0f, -4f, 0f, -4f);
        containersPane.setScrollingDisabled(false, true);

        containerExitButton = new ImageButton(Gui.skin, "imagebutton-inventorycontent-close") {
            @Override
            public float getPrefWidth() {
                return 64f;
            }

            @Override
            public float getPrefHeight() {
                return 64f;
            }
        };
        containerExitButton.background("button-inventory-2-9p").pad(0f, 4f, 0f, 4f);
        containerExitButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                containerStack.pop();
                if (containerStack.empty()) {
                    hide();
                } else {
                    recreate();
                }
            }
        });
    }

    @Override
    public void recreate() {
        sumWidth = 0f;
        containerGroup.clearChildren();
        containerGroup.addActor(containerExitButton);

        entriesTable.clearChildren();
        EntityStorage storage = containerStack.peek();

        DoubleLinkedList.DoubleLinkedListNodeIterator<EntityStorage.EntityStack> iter = storage.stacks.nodeIterator();
        while (iter.hasNext()) {
            DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node = iter.next();

            lastSlot = new StorageSlotView(storage, node);
            addInventoryButton(lastSlot);
        }
    }

    public void addInventoryButton(StorageSlotView slot) {
        entriesTable.add(new InventorySlotButton(slot)).align(Align.topLeft);
        if (slot.hasStorageEntity()) {
            containerGroup.addActor(new InventoryContainerButton(slot));
        }
    }

    public void removeInventoryButton(StorageSlotView slot) {
        for (Actor button : entriesTable.getChildren()) {
            if (button instanceof InventorySlotButton &&
                    slot.equals(((InventorySlotButton) button).slot)) {
                button.remove();
                break;
            }
        }

        removeInventoryContainerButton(slot);
    }

    public void removeInventoryContainerButton(StorageSlotView slot) {
        for (Actor button : containerGroup.getChildren()) {
            if (button instanceof InventoryContainerButton &&
                    slot.equals(((InventoryContainerButton) button).slot)) {
                button.remove();
                break;
            }
        }
    }

    public void appendNewSlots() {
        DoubleLinkedList.DoubleLinkedListNode<EntityStorage.EntityStack> node = lastSlot.getNode();
        while ((node = node.getNext()) != null) {
            addInventoryButton(new StorageSlotView(lastSlot.getStorage(), node));
        }
    }

    @Override
    public void layout() {
        // awful
        try {
            sumWidth = 0;
            int row = 0, column = 0, maxColumn = 0;
            Cell<?> previousCell = null;
            for (Cell<?> cell : new Array.ArrayIterator<>(entriesTable.getCells())) {
                if ((sumWidth += cell.getPrefWidth()) >= entriesCell.getActorWidth()) {
                    maxColumn = Math.max(maxColumn, column);
                    column = 0;
                    row++;

                    if (previousCell != null) {
                        fieldCellEndRow.setBoolean(previousCell, true);
                    }

                    sumWidth = cell.getPrefWidth();
                } else if (previousCell != null) {
                    fieldCellEndRow.setBoolean(previousCell, false);
                }

                fieldCellRow.setInt(cell, row);
                fieldCellColumn.setInt(cell, column);
                column++;
                previousCell = cell;
            }
            fieldTableRows.setInt(entriesTable, row + 1);
            fieldTableColumns.setInt(entriesTable, maxColumn);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        entriesTable.invalidate();
        super.layout();
    }

    public void show(EntityStorage storage) {
        containerStack.clear();
        containerStack.push(storage);
        show();
    }

    public void resize() {
        if (wrapper.hasParent()) {
            recreate();
        }
    }

    @Override
    public void hide() {
        super.hide();
        InventorySlotButton.unselectAll();
    }
}
