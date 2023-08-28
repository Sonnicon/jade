package sonnicon.jade.util;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.EventHandler;
import sonnicon.jade.generated.EventTypes;

import java.util.HashMap;
import java.util.LinkedList;

@EventGenerator(id = "Any", param = {Class.class}, label = {"eventType"})
public class Events {
    private final HashMap<Class<? extends EventHandler>, LinkedList<EventHandler>> handlers = new HashMap<>();

    public void register(EventHandler... handles) {
        for (EventHandler handler : handles) {
            LinkedList<EventHandler> list = handlers.get(handler.getType());
            if (list != null) {
                list.add(handler);
            } else {
                list = new LinkedList<>();
                list.add(handler);
                handlers.put(handler.getType(), list);
            }
        }
    }

    public void unregister(EventHandler... handles) {
        for (EventHandler handler : handles) {
            LinkedList<EventHandler> list = handlers.get(handler.getType());
            if (list != null) {
                list.remove(handler);
            }
        }
    }

    public void handle(Class<? extends EventHandler> handleKey, Object... values) {
        LinkedList<EventHandler> list = handlers.get(handleKey);
        if (list != null) {
            list.forEach(cons -> cons.applyInternal(values));
        }
        if (handleKey != EventTypes.AnyEvent.class) {
            handle(EventTypes.AnyEvent.class, handleKey);
        }
    }
}
