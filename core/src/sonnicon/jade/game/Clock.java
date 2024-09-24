package sonnicon.jade.game;

import sonnicon.jade.game.actions.Actions;

import java.util.ArrayList;
import java.util.LinkedList;

public class Clock {
    public static LinkedList<ITicking> ticking = new LinkedList<>();
    public static LinkedList<IUpdate> updating = new LinkedList<>();

    private static final ArrayList<IClocked> listAdd = new ArrayList<>();
    private static final ArrayList<IClocked> listRemove = new ArrayList<>();

    private static float tickNum = 0f, updateNum = 0f;

    private static float advanceTo = 0f, lastTick = 0f;
    private static final float tickInterpRate = 1f;

    //todo make this nicer
    public static void register(IClocked target) {
        listAdd.add(target);
    }

    public static void unregister(IClocked target) {
        listRemove.add(target);
    }

    // In-game action step
    public static void tick(float delta) {
        advanceTo += delta;
    }

    public static void tickFast(float delta) {
        advanceTo += delta;
        tickNum += delta;
        tickInternal();
    }

    // Render step
    public static void update(float delta) {
        // Updates
        updateNum += delta;
        updating.forEach(t -> t.update(delta));

        // Ticks
        if (advanceTo > tickNum) {
            float advancement = Float.min(advanceTo - tickNum, tickInterpRate * delta);
            while (advancement > 0f) {

                float shortestAvailable = Actions.shortest() - tickNum;
                if (shortestAvailable <= advancement) {
                    advancement -= shortestAvailable;
                    tickNum += shortestAvailable;
                    tickInternal();
                } else {
                    tickNum += advancement;
                    advancement = 0f;
                }
            }
        }

        // Add new handlers
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

    private static void tickInternal() {
        float delta = tickNum - lastTick;
        ticking.forEach(t -> t.tick(delta));
        lastTick = tickNum;
    }

    public static float getTickNum() {
        return tickNum;
    }

    public static float getUpdateNum() {
        return updateNum;
    }

    public static float getTickInterp() {
        return tickNum;
    }

    public static boolean isTickRemaining() {
        return tickNum < advanceTo;
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
