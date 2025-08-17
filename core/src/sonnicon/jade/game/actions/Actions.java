package sonnicon.jade.game.actions;

import sonnicon.jade.game.Clock;
import sonnicon.jade.util.IDebuggable;
import sonnicon.jade.util.ObjectPool;
import sonnicon.jade.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Actions implements Clock.IOnTick, Clock.IOnFrame, IDebuggable {
    public static final ArrayList<Action> actionsList = new ArrayList<>();
    private static final ArrayList<Action> newActions = new ArrayList<>();

    public static Actions actions;
    protected static int currentInterruptions = 0;

    public static void init() {
        Clock.register(actions = new Actions());
    }

    @Override
    public void onAlign(float delta) {
        actionsList.forEach(Action::onAlign);
    }

    @Override
    public void onTick(float delta) {
        //todo cache nearest time finish

        // Tick every action
        actionsList.forEach(Action::onTick);

        // Deal with interrupts and finishing
        Iterator<Action> iterator = actionsList.iterator();
        while (iterator.hasNext()) {
            Action action = iterator.next();
            if (action.interrupted) {
                currentInterruptions--;
                iterator.remove();
                action.onInterrupt();
                action.freeRecursively();
            } else if (Clock.getTickNum() >= action.getTimeFinish()) {
                iterator.remove();
                action.onFinish();
                newActions.addAll(action.then);
                action.free();
            }
        }
        newActions.forEach(Action::start);
        newActions.clear();
    }

    public static <T extends Action> T obtain(Class<T> type) {
        return ObjectPool.obtain(type);
    }

    public static float getEarliestTimeFinish() {
        if (currentInterruptions > 0) {
            return 0f;
        } else {
            return (float) actionsList.stream().mapToDouble(Action::getTimeFinish).min().orElse(Float.MAX_VALUE);
        }
    }

    @Override
    public Map<Object, Object> debugProperties() {
        return Utils.mapFrom(
                "actionList", actionsList,
                "getEarliestTimeFinish()", getEarliestTimeFinish());
    }

    @Override
    public void onFrame(float delta) {
        for (Action action : actionsList) {

            action.onFrame();
        }
    }

    // You are not allowed to keep or re-use these
    public abstract static class Action implements IDebuggable, ObjectPool.IPooledObject {
        public float timeStart;
        public float duration;
        public boolean interrupted = false;
        public ArrayList<Action> then = new ArrayList<>();

        protected abstract void onStart();

        protected abstract void onFinish();

        protected abstract void onInterrupt();

        protected abstract void onFrame();

        protected abstract void onAlign();

        protected abstract void onTick();

        public final Action setDuration(float duration) {
            this.duration = duration;
            return this;
        }

        public final float getProgress() {
            return (Clock.getTickNum() - timeStart) / duration;
        }

        public final Action start() {
            timeStart = Clock.getTickNum();
            actionsList.add(this);
            onStart();
            return this;
        }

        public final void free() {
            then.clear();
            interrupted = false;
            ObjectPool.free(this);
        }

        public final float getTimeFinish() {
            return timeStart + duration;
        }

        public final Action then(Action action) {
            then.add(action);
            return action;
        }

        public final void then(Action... actions) {
            Arrays.stream(actions).forEach(Action::then);
        }

        public final void interrupt() {
            //todo flesh out interruptions
            assert (Actions.actionsList.contains(this));
            interrupted = true;
            Actions.currentInterruptions++;
        }

        protected final void freeRecursively() {
            free();
            for (Action action : then) {
                action.freeRecursively();
            }
        }

        @Override
        public Map<Object, Object> debugProperties() {
            return Utils.mapFrom(
                    "timeStart", timeStart,
                    "duration", duration,
                    "then", then);
        }
    }
}
