package sonnicon.jade.game;

import sonnicon.jade.game.actions.Actions;

import java.util.ArrayList;
import java.util.LinkedList;

public class Clock {
    //todo dont linkedlist, just use iterators
    public static LinkedList<IOnTick> onTickList = new LinkedList<>();
    public static LinkedList<IOnFrame> onFrameList = new LinkedList<>();

    private static final ArrayList<IOnTick> tickListAdd = new ArrayList<>();
    private static final ArrayList<IOnTick> tickListRemove = new ArrayList<>();
    private static final ArrayList<IOnFrame> frameListAdd = new ArrayList<>();
    private static final ArrayList<IOnFrame> frameListRemove = new ArrayList<>();

    private static float tickNum = 0f, frameNum = 0f;

    private static float advanceTo = 0f, lastTick = 0f;
    public static float tickInterpRate = 1f;
    private static final float TICK_MIN_PERIOD = 0.4f;

    protected static ClockPhase phase;

    public static ClockPhase getPhase() {
        return phase;
    }

    //todo make this nicer
    public static void register(IClocked target) {
        if (target instanceof IOnTick) {
            tickListAdd.add((IOnTick) target);
        }
        if (target instanceof IOnFrame) {
            frameListAdd.add((IOnFrame) target);
        }
    }

    public static void unregister(IClocked target) {
        if (target instanceof IOnTick) {
            tickListRemove.add((IOnTick) target);
        }
        if (target instanceof IOnFrame) {
            frameListRemove.add((IOnFrame) target);
        }
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
        phase = ClockPhase.frame;
        onFrameList.forEach(t -> t.onFrame(delta));
        phase = ClockPhase.none;

        // Ticks
        if (advanceTo > tickNum) {
            float advancement = Float.min(advanceTo - tickNum, tickInterpRate * delta);

            while (advancement > 0.001f) {
                float timeToNextTick = getTimeToNextTick();
                if (timeToNextTick <= advancement) {
                    advancement -= timeToNextTick;
                    tickNum += timeToNextTick + 0.001f;
                    tickInternal();
                } else {
                    tickNum += advancement;
                    advancement = 0f;
                }
            }
        }

        // Add new handlers
        // Needs to be during frames since particles can disappear between ticks
        if (!frameListAdd.isEmpty()) {
            onFrameList.addAll(frameListAdd);
            frameListAdd.clear();
        }
        if (!frameListRemove.isEmpty()) {
            onFrameList.removeAll(frameListRemove);
            frameListRemove.clear();
        }
    }

    private static void tickInternal() {
        float delta = tickNum - lastTick;

        phase = ClockPhase.align;
        onTickList.forEach(t -> t.onAlign(delta));
        phase = ClockPhase.tick;
        onTickList.forEach(t -> t.onTick(delta));
        phase = ClockPhase.none;

        if (!tickListAdd.isEmpty()) {
            onTickList.addAll(tickListAdd);
            tickListAdd.clear();
        }
        if (!tickListRemove.isEmpty()) {
            onTickList.removeAll(tickListRemove);
            tickListRemove.clear();
        }
        lastTick = tickNum;
    }

    public static float getTickNum() {
        return tickNum;
    }

    public static float getFrameNum() {
        return frameNum;
    }

    public static float getTimeToNextTick() {
        //todo getTimeOfNextTick
        //todo cache this
        float shortestAvailable = Actions.getEarliestTimeFinish() - tickNum;
        float minPeriodRate = TICK_MIN_PERIOD - (tickNum % TICK_MIN_PERIOD);
        return Math.min(minPeriodRate, shortestAvailable);
    }

    public static boolean isTickRemaining() {
        return tickNum < advanceTo;
    }

    private interface IClocked {

    }

    public interface IOnTick extends IClocked {
        default void onAlign(float delta) {
        }

        void onTick(float delta);
    }

    public interface IOnFrame extends IClocked {
        void onFrame(float delta);
    }

    // Mainly for assertions
    public enum ClockPhase {
        frame,
        align,
        tick,
        none
    }
}
