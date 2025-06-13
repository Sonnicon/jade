package sonnicon.jade.game;

import sonnicon.jade.game.actions.Actions;

import java.util.ArrayList;
import java.util.LinkedList;

public class Clock {
    //todo dont linkedlist, just use iterators
    public static LinkedList<IOnTick> onTickList = new LinkedList<>();
    public static LinkedList<IOnFrame> onFrameList = new LinkedList<>();

    private static final ArrayList<IClocked> listAdd = new ArrayList<>();
    private static final ArrayList<IClocked> listRemove = new ArrayList<>();

    private static float tickNum = 0f, frameNum = 0f;

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
    public static void frame(float delta) {
        // Updates
        frameNum += delta;
        onFrameList.forEach(t -> t.onFrame(delta));

        // Ticks
        if (advanceTo > tickNum) {
            float advancement = Float.min(advanceTo - tickNum, tickInterpRate * delta);
            while (advancement > 0f) {

                float shortestAvailable = Actions.getEarliestTimeFinish() - tickNum;
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
                if (target instanceof IOnTick) {
                    onTickList.add((IOnTick) target);
                }
                if (target instanceof IOnFrame) {
                    onFrameList.add((IOnFrame) target);
                }
            }
            listAdd.clear();
        }

        if (!listRemove.isEmpty()) {
            for (IClocked target : listRemove) {
                if (target instanceof IOnTick) {
                    onTickList.remove((IOnTick) target);
                }
                if (target instanceof IOnFrame) {
                    onFrameList.remove((IOnFrame) target);
                }
            }
            listRemove.clear();
        }
    }

    private static void tickInternal() {
        float delta = tickNum - lastTick;
        onTickList.forEach(t -> t.onTick(delta));
        lastTick = tickNum;
    }

    public static float getTickNum() {
        return tickNum;
    }

    public static float getFrameNum() {
        return frameNum;
    }

    public static float getTickInterp() {
        return tickNum;
    }

    public static boolean isTickRemaining() {
        return tickNum < advanceTo;
    }

    private interface IClocked {

    }

    public interface IOnTick extends IClocked {
        void onTick(float delta);
    }

    public interface IOnFrame extends IClocked {
        void onFrame(float delta);
    }
}
