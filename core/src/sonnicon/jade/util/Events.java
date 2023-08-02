package sonnicon.jade.util;

import sonnicon.jade.EventHandler;

import java.util.HashMap;
import java.util.LinkedList;

public class Events {
    private final HashMap<Class<? extends EventHandler>, LinkedList<EventHandler>> handlers = new HashMap<>();

    public void register(Class<? extends EventHandler> key, EventHandler handler) {
        //todo remove key
        LinkedList<EventHandler> list = handlers.get(key);
        if (list != null) {
            list.add(handler);
        } else {
            list = new LinkedList<>();
            list.add(handler);
            handlers.put(key, list);
        }
    }

    public void unregister(Class<? extends EventHandler> key, EventHandler handler) {
        LinkedList<EventHandler> list = handlers.get(key);
        if (list != null) {
            list.remove(handler);
        }
    }

    public void handle(Class<? extends EventHandler> key, Object... values) {
        handle(key, key, values);
    }

    private void handle(Class<? extends EventHandler> handleKey, Class<? extends EventHandler> passKey, Object... values) {
        LinkedList<EventHandler> list = handlers.get(handleKey);
        if (list != null) {
            list.forEach(cons -> cons.applyInternal(passKey, values));
        }
        if (handleKey != null) {
            handle(null, passKey, values);
        }
    }
}
