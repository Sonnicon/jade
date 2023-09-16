package sonnicon.jade.game;

import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Actions implements Clock.ITicking, IDebuggable {
    public static ArrayList<Action> actionsList = new ArrayList<>();
    public static Actions actions;

    public static void init() {
        Clock.register(actions = new Actions());
    }

    @Override
    public void tick(float delta) {
        process(delta);
    }

    public static void enqueue(Action action) {
        if (action.isQueued) {
            return;
        }

        int l = 0, r = actionsList.size() - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            float mFinish = actionsList.get(m).timeFinish;
            if (mFinish < action.timeFinish) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        actionsList.add(l, action);
        action.isQueued = true;
    }

    public static void dequeue(Action action) {
        actionsList.remove(action);
    }

    public static void process(float delta) {
        float deltaEnd = Clock.getTickNum() + delta;
        Iterator<Action> iter = actionsList.iterator();
        while (iter.hasNext()) {
            Action action = iter.next();
            if (deltaEnd >= action.timeFinish) {
                action.finish();
                iter.remove();
            } else {
                break;
            }
        }
    }


    private static final HashMap<Class<? extends Action>, ArrayList<Action>> ACTION_STORE = new HashMap<>();

    public static <T extends Action> T actionObtain(Class<T> type) {
        ArrayList<? extends Action> list = ACTION_STORE.get(type);
        if (list == null || list.isEmpty()) {
            try {
                return (T) type.newInstance().reset();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return (T) list.remove(0).reset();
        }
    }

    public static void actionFree(Action action) {
        Class<? extends Action> clazz = action.getClass();
        ArrayList<Action> list = ACTION_STORE.computeIfAbsent(clazz, k -> new ArrayList<>());
        list.add(action);
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("actions", actionsList);
    }

    public abstract static class Action implements IDebuggable {
        public float timeFinish;
        public boolean keepRef, isQueued;

        final Action reset() {
            this.keepRef = false;
            this.isQueued = false;
            return this;
        }

        public void finish() {
            if (!keepRef) {
                actionFree(this);
            } else {
                isQueued = false;
            }
        }

        public Action time(float duration) {
            timeFinish = Clock.getTickNum() + duration;
            return this;
        }

        public Action enqueue() {
            Actions.enqueue(this);
            return this;
        }

        public Action keepRef() {
            keepRef = true;
            return this;
        }

        @Override
        public Map<Object, Object> debugProperties() {
            return Utils.mapFrom("timeFinish", timeFinish, "keepRef", keepRef, "isQueued", isQueued);
        }
    }
}
