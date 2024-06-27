package sonnicon.jade.game;

import java.util.ArrayList;
import java.util.LinkedList;

public class Clock {
    public static LinkedList<ITicking> ticking = new LinkedList<>();
    public static LinkedList<IUpdate> updating = new LinkedList<>();

    private static final ArrayList<IClocked> listAdd = new ArrayList<>();
    private static final ArrayList<IClocked> listRemove = new ArrayList<>();

    private static float tickNum = 0f, updateNum = 0f;

    private static float tickInterp = 0f, tickRemaining = 0f, lastDelta;
    private static final float tickInterpRate = 1 / 60f;

    //todo make this nicer
    public static void register(IClocked target) {
        listAdd.add(target);
    }

    public static void unregister(IClocked target) {
        listRemove.add(target);
    }

    // In-game action step
    public static void tick(float delta) {
        tickRemaining += delta;
        lastDelta += delta;
    }

    public static void tickFast(float delta) {
        tickInterp += delta;
        lastDelta = delta;
        tickInternal(delta);
    }

    // Render step
    public static void update(float delta) {
        updateNum += delta;
        updating.forEach(t -> t.update(delta));

        if (tickRemaining > 0f) {
            float d = Float.min(tickRemaining, tickInterpRate);
            tickRemaining -= d;
            tickInterp += d;

            if (tickRemaining <= 0.0001f) {
                tickRemaining = 0f;
                tickInterp = tickNum + lastDelta;
                tickInternal(lastDelta);
            }
        }

        if (!listAdd.isEmpty()) {
            for (IClocked target : listAdd) {
                if (target instanceof ITicking) {
                    ticking.add((ITicking) target);
                }
                if (target instanceof IUpdate) {
                    updating.add((IUpdate) target);
                }
            }
            listAdd.clear();
        }

        if (!listRemove.isEmpty()) {
            for (IClocked target : listRemove) {
                if (target instanceof ITicking) {
                    ticking.remove((ITicking) target);
                }
                if (target instanceof IUpdate) {
                    updating.remove((IUpdate) target);
                }
            }
            listRemove.clear();
        }
    }

    private static void tickInternal(float delta) {
        tickNum += delta;
        ticking.forEach(t -> t.tick(delta));
    }

    public static float getTickNum() {
        return tickNum;
    }

    public static float getUpdateNum() {
        return updateNum;
    }

    public static float getTickInterp() {
        return tickInterp;
    }

    public static boolean isTickRemaining() {
        return tickRemaining > 0f;
    }

    private interface IClocked {

    }

    public interface ITicking extends IClocked {
        void tick(float delta);
    }

    public interface IUpdate extends IClocked {
        void update(float delta);
    }
}
