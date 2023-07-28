package sonnicon.jade;

@FunctionalInterface
public interface EventHandler {
    void applyInternal(Class<? extends EventHandler> type, Object... objs);
}
