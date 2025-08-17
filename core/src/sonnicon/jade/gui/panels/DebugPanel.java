package sonnicon.jade.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import sonnicon.jade.Jade;
import sonnicon.jade.content.Content;
import sonnicon.jade.entity.components.player.PlayerControlComponent;
import sonnicon.jade.game.Clock;
import sonnicon.jade.game.actions.Actions;
import sonnicon.jade.gui.Gui;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class DebugPanel extends Panel {
    protected Stack<Object> targets = new Stack<>();

    protected Label labelName;

    protected Table tableContents;

    private static final Set<Class<?>> PURE_TYPES = Utils.setFrom(Integer.class, Byte.class, Short.class, Long.class, Float.class, Double.class, Boolean.class, Character.class, String.class);
    private static final DebugGlobal DEBUG_GLOBAL = new DebugGlobal();

    public DebugPanel() {
    }

    @Override
    public void create() {
        super.create();
        wrapper.pad(32f, 32f, 32f, 32f);
        cell.maxSize(800f, 1000f);

        background("panel-inventory-rounded-9p");

        Table tableTitle = new Table();
        ScrollPane titleScrollPane = new ScrollPane(tableTitle);
        titleScrollPane.setScrollingDisabled(false, true);
        add(titleScrollPane).growX().padLeft(4f).top();


        labelName = new Label("", Gui.skin);
        labelName.setFontScale(1.2f);
        tableTitle.add(labelName).left().expandX().padLeft(12f);

        ImageButton buttonGlobal = new ImageButton(Gui.skin, "imagebutton-inventory-control-world");
        buttonGlobal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                show(DEBUG_GLOBAL);
                recreate();
            }
        });

        ImageButton buttonRefresh = new ImageButton(Gui.skin, "imagebutton-inventory-control-refresh");
        buttonRefresh.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                recreate();
            }
        });

        ImageButton buttonBack = new ImageButton(Gui.skin, "imagebutton-inventory-control-back");
        buttonBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targets.pop();
                recreate();
            }
        });

        ImageButton buttonClose = new ImageButton(Gui.skin, "imagebutton-inventory-control-close");
        buttonClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        Table buttonsTable = new Table();
        buttonsTable.add(buttonGlobal);
        buttonsTable.add(buttonRefresh);
        buttonsTable.add(buttonBack);
        buttonsTable.add(buttonClose);
        add(buttonsTable).top().right().row();

        Table contentsWrapper = new Table();
        add(contentsWrapper).grow().colspan(2).padTop(4f);

        tableContents = new Table(Gui.skin);
        tableContents.top().left();
        tableContents.background("panel-debug-9p");
        ScrollPane paneDescription = new ScrollPane(tableContents);
        contentsWrapper.add(paneDescription).grow();
    }

    @Override
    protected void recreate() {
        if (targets.isEmpty()) {
            hide();
            return;
        }

        Object target = targets.peek();
        labelName.setText(IDebuggable.debugName(target));

        tableContents.clearChildren();
        int rowNum = 0;
        for (Map.Entry<Object, Object> entry : IDebuggable.debugProperties(target).entrySet()) {
            Table rowTable = new Table(Gui.skin);
            if ((rowNum++ & 1) == 0) {
                rowTable.background("dark-10");
            }

            rowTable.add(createEntry(entry.getKey())).left().expandX().pad(0f, 4f, 0f, 4f);
            rowTable.add(createEntry(entry.getValue())).right().pad(0f, 4f, 0f, 4f);

            tableContents.add(rowTable).colspan(2).growX().left();
            tableContents.row();
        }

        if (target instanceof IDebuggable) {
            IDebuggable debuggable = (IDebuggable) target;
            Map<Object, Runnable> actionMap = debuggable.debugActions();
            if (actionMap != null) {
                for (Map.Entry<Object, Runnable> entry : actionMap.entrySet()) {
                    Table rowTable = new Table(Gui.skin);
                    if ((rowNum++ & 1) == 0) {
                        rowTable.background("dark-10");
                    }

                    Actor result;
                    String buttonText;
                    if (PURE_TYPES.contains(entry.getKey().getClass())) {
                        buttonText = entry.getKey().toString();
                    } else {
                        buttonText = IDebuggable.debugName(entry.getKey());
                    }
                    result = new TextButton(buttonText, Gui.skin, "debug");
                    result.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            entry.getValue().run();
                            recreate();
                        }
                    });

                    rowTable.add(result).left().expandX().pad(0f, 4f, 0f, 4f);
                    tableContents.add(rowTable).colspan(1).growX().left();
                    tableContents.row();
                }
            }


        }
    }

    private Actor createEntry(Object target) {
        Actor result;
        if (target == null) {
            result = new Label("null", Gui.skin);
        } else if (PURE_TYPES.contains(target.getClass())) {
            result = new Label(target.toString(), Gui.skin);
        } else {
            result = new TextButton("[" + IDebuggable.debugName(target) + "]", Gui.skin, "debug");
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
        // Clearing here might keep it not GCed, but it makes debugging easier
        targets.clear();
        targets.push(target);
        show();
    }

    @Override
    public void hide() {
        super.hide();
    }

    private static final class DebugGlobal implements IDebuggable {

        @Override
        public String debugName() {
            return "Global";
        }

        @Override
        public Map<Object, Object> debugProperties() {
            return Utils.mapFrom(
                    "actions", Actions.actions,
                    "clock_ticking", Clock.onTickList,
                    "clock_updating", Clock.onFrameList,
                    "clock_tick", Clock.getTickNum(),
                    "clock_update", Clock.getFrameNum(),
                    "controlled", PlayerControlComponent.getEntity(),
                    "renderer", Jade.renderer,
                    "viewOverlay", Content.viewOverlay,
                    "world", Content.world,
                    "Clock tickInterpRate", Clock.tickInterpRate
            );
        }

        @Override
        public Map<Object, Runnable> debugActions() {
            return Utils.mapFrom(
                    "Set tickInterpRate +0.1f", (Runnable) () -> Clock.tickInterpRate += 0.1f,
                    "Set tickInterpRate -0.1f", (Runnable) () -> Clock.tickInterpRate -= 0.1f
            );
        }
    }
}
