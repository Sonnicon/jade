package sonnicon.jade.util;

import sonnicon.jade.EventGenerator;
import sonnicon.jade.EventHandler;
import sonnicon.jade.generated.EventTypes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

//todo why is this a linkedlist??

@EventGenerator(id = "Any", param = {Class.class}, label = {"eventType"})
public class Events implements IDebuggable {
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

    @Override
    public Map<Object, Object> debugProperties() {
        return handlers.entrySet().stream()
                .collect(Collectors.toMap(e ->
                                e.getKey().getSimpleName(),
                        Map.Entry::getValue));
    }
}
