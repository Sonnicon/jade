package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Structs;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class DebugPanel extends Panel {
    protected Stack<Object> targets = new Stack<>();

    protected Label labelName;

    protected Table tableContents;

    private static final Set<Class<?>> PURE_TYPES = Structs.setFrom(Integer.class, Byte.class, Short.class, Long.class, Float.class, Double.class, Boolean.class, Character.class, String.class);

    public DebugPanel() {
    }

    @Override
    public void create() {
        super.create();
        wrapper.pad(32f, 32f, 32f, 32f);
        cell.maxSize(800f, 1000f);

        background("button-inventory-1-9p");

        Table tableTitle = new Table();
        ScrollPane titleScrollPane = new ScrollPane(tableTitle);
        titleScrollPane.setScrollingDisabled(false, true);
        add(titleScrollPane).growX().padLeft(4f).top();


        labelName = new Label("", Gui.skin);
        labelName.setFontScale(1.2f);
        tableTitle.add(labelName).left().expandX().padLeft(12f);

        ImageButton buttonClose = new ImageButton(Gui.skin, "imagebutton-inventorycontent-close");
        buttonClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        ImageButton buttonBack = new ImageButton(Gui.skin, "imagebutton-inventorycontent-back");
        buttonBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targets.pop();
                recreate();
            }
        });

        Table buttonsTable = new Table();
        buttonsTable.add(buttonBack);
        buttonsTable.add(buttonClose);
        add(buttonsTable).top().right().row();

        Table contentsWrapper = new Table();
        add(contentsWrapper).grow().colspan(2);

        tableContents = new Table(Gui.skin);
        tableContents.top().left();
        tableContents.background("button-inventory-2-9p");
        ScrollPane paneDescription = new ScrollPane(tableContents);
        contentsWrapper.add(paneDescription).grow();
    }

    @Override
    protected void recreate() {
        labelName.setText(IDebuggable.debugName(targets.peek()));

        tableContents.clearChildren();
        int rowNum = 0;
        for (Map.Entry<Object, Object> entry : IDebuggable.debugProperties(targets.peek()).entrySet()) {
            Table rowTable = new Table(Gui.skin);
            if ((rowNum++ & 1) == 0) {
                rowTable.background("dark-10");
            }

            rowTable.add(createEntry(entry.getKey())).left().expandX().pad(0f, 4f, 0f, 4f);
            rowTable.add(createEntry(entry.getValue())).right().pad(0f, 4f, 0f, 4f);

            tableContents.add(rowTable).colspan(2).growX().left();
            tableContents.row();
        }
    }

    private Actor createEntry(Object target) {
        Actor result;
        if (PURE_TYPES.contains(target.getClass())) {
            result = new Label(target.toString(), Gui.skin);
        } else {
            result = new TextButton("[" + IDebuggable.debugName(target) + "]", Gui.skin);
            result.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    show(target);
                }
            });
        }
        return result;
    }

    public void show(Object target) {
        targets.push(target);
        show();
    }

    @Override
    public void hide() {
        super.hide();
        targets.clear();
    }
}