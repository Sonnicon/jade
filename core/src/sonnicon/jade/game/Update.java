package sonnicon.jade.game;

import java.util.LinkedList;

public class Update {
    protected static LinkedList<IUpdate> updating = new LinkedList<>();

    public static void register(IUpdate update) {
        updating.add(update);
    }

    public static void unregister(IUpdate update) {
        updating.remove(update);
    }

    public static void update(float delta) {
        updating.forEach(update -> update.update(delta));
    }

    public interface IUpdate {
        void update(float delta);
    }
}
