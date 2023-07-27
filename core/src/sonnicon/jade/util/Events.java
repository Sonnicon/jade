package sonnicon.jade.util;

import java.util.HashMap;
import java.util.LinkedList;

public class Events<K> {
    //todo I REALLY NEED TO MAKE THIS TYPED EVENT PARAMS
    private final HashMap<K, LinkedList<Consumer2<K, Object[]>>> handlers = new HashMap<>();

    public void register(K key, Consumer2<K, Object[]> handler) {
        LinkedList<Consumer2<K, Object[]>> list = handlers.get(key);
        if (list != null) {
            list.add(handler);
        } else {
            list = new LinkedList<>();
            list.add(handler);
            handlers.put(key, list);
        }
    }

    public void unregister(K key, Consumer2<K, Object[]> handler) {
        LinkedList<Consumer2<K, Object[]>> list = handlers.get(key);
        if (list != null) {
            list.remove(handler);
        }
    }

    public void handle(K key, Object... values) {
        handle(key, key, values);
    }

    private void handle(K handleKey, K passKey, Object... values) {
        LinkedList<Consumer2<K, Object[]>> list = handlers.get(handleKey);
        if (list != null) {
            list.forEach(cons -> cons.apply(passKey, values));
        }
        if (handleKey != null) {
            handle(null, passKey, values);
        }
    }
}
