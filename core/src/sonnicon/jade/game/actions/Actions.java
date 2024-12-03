package sonnicon.jade.game.actions;

import sonnicon.jade.game.Clock;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
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
        action.onStart();
    }

    public static void dequeue(Action action) {
        actionsList.remove(action);
    }

    public static void process(float delta) {
        while (!actionsList.isEmpty()) {
            Action action = actionsList.get(0);
            if (Clock.getTickNum() >= action.timeFinish) {
                action.finish();
            } else {
                break;
            }
        }
    }

    public static <T extends Action> T obtain(Class<T> type) {
        return (T) ObjectPool.obtain(type).reset();
    }

    public static float shortest() {
        if (actionsList.isEmpty()) {
            return Float.MAX_VALUE;
        } else {
            return actionsList.get(0).timeFinish;
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom("actions", actionsList);
    }

    public abstract static class Action implements IDebuggable, ObjectPool.IPooledObject {
        public float timeFinish;
        public boolean keepRef, isQueued;
        private float duration;

        public final Action reset() {
            this.keepRef = false;
            this.isQueued = false;
            return this;
        }

        public final void finish() {
            interrupt();
            onFinish();
        }

        protected abstract void onStart();

        protected abstract void onFinish();

        public final Action time(float duration) {
            this.duration = duration;
            return this;
        }

        public final Action enqueue() {
            timeFinish = Clock.getTickNum() + duration;
            Actions.enqueue(this);
            return this;
        }

        public final Action keepRef() {
            keepRef = true;
            return this;
        }

        public final Action dequeue() {
            Actions.dequeue(this);
            return this;
        }

        public void interrupt() {
            if (!keepRef) {
                free();
            } else {
                dequeue();
                isQueued = false;
            }
        }

        public final void free() {
            dequeue();
            ObjectPool.free(this);
        }

        @Override
        public Map<Object, Object> debugProperties() {
            return Utils.mapFrom("timeFinish", timeFinish, "keepRef", keepRef, "isQueued", isQueued);
        }
    }
}
