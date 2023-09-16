package sonnicon.jade.game;

import java.util.LinkedList;

public class Clock {
    public static LinkedList<ITicking> ticking = new LinkedList<>();
    public static LinkedList<IUpdate> updating = new LinkedList<>();

    private static float tickNum = 0f, updateNum = 0f;

    //todo make this nicer
    public static void register(IClocked target) {
        if (target instanceof ITicking) {
            ticking.add((ITicking) target);
        }
        if (target instanceof IUpdate) {
            updating.add((IUpdate) target);
        }
    }

    public static void unregister(IClocked target) {
        if (target instanceof ITicking) {
            ticking.remove((ITicking) target);
        }
        if (target instanceof IUpdate) {
            updating.remove((IUpdate) target);
        }
    }

    public static void tick(float delta) {
        tickNum += delta;
        ticking.forEach(t -> t.tick(delta));
    }

    public static void update(float delta) {
        updateNum += delta;
        updating.forEach(t -> t.update(delta));
    }

    public static float getTickNum() {
        return tickNum;
    }

    public static float getUpdateNum() {
        return updateNum;
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
