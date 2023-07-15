package sonnicon.jade.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Consumer;

public class Events<K> {
    private final HashMap<K, LinkedList<Consumer<Object[]>>> handlers = new HashMap<>();

    public void register(K key, Consumer<Object[]> handler) {
        LinkedList<Consumer<Object[]>> list = handlers.get(key);
        if (list != null) {
            list.add(handler);
        } else {
            list = new LinkedList<>();
            list.add(handler);
            handlers.put(key, list);
        }
    }

    public void unregister(K key, Consumer<Object[]> handler) {
        LinkedList<Consumer<Object[]>> list = handlers.get(key);
        if (list != null) {
            list.remove(handler);
        }
    }

    public void handle(K key, Object... values) {
        LinkedList<Consumer<Object[]>> list = handlers.get(key);
        if (list != null) {
            list.forEach(cons -> cons.accept(values));
        }
        if (key != null) {
            handle(null, values);
        }
    }
}
