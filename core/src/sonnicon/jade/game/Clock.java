package sonnicon.jade.game;

import java.util.LinkedList;

public class Clock {
    protected static LinkedList<ITicking> ticking = new LinkedList<>();
    protected static LinkedList<IUpdate> updating = new LinkedList<>();

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
        ticking.forEach(t -> t.tick(delta));
    }

    public static void update(float delta) {
        updating.forEach(t -> t.update(delta));
    }

    private interface IClocked {

    }

    public interface ITicking extends IClocked {
        void tick(float delta);
    }

    public interface IUpdate extends IClocked{
        void update(float delta);
    }
}
