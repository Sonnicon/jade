package sonnicon.jade;

public interface EventHandler {
    void applyInternal(Object... objs);

    Class<? extends EventHandler> getType();
}
