package sonnicon.jade.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface IDebuggable {
    default String debugName() {
        return toString().replace("sonnicon.jade.", "");
    }

    Map<Object, Object> debugProperties();

    static String debugName(Object target) {
        if (target instanceof IDebuggable) {
            return ((IDebuggable) target).debugName();
        }

        return target.getClass().getName() + "@" + Integer.toHexString(target.hashCode());
    }

    static Map<Object, Object> debugProperties(Object target) {
        if (target instanceof IDebuggable) {
            return ((IDebuggable) target).debugProperties();
        }

        if (target instanceof Map) {
            return (Map<Object, Object>) target;
        }

        HashMap<Object, Object> result = new HashMap<>();
        if (target instanceof Iterable) {
            Iterator<?> iter = ((Iterable<?>) target).iterator();
            for (int i = 0; iter.hasNext(); i++) {
                result.put(String.valueOf(i), iter.next());
            }
        } else if (target.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(target); i++) {
                result.put(String.valueOf(i), Array.get(target, i));
            }
        }


        return result;
    }
}
