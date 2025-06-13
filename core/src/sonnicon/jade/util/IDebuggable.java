package sonnicon.jade.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface IDebuggable {
    default String debugName() {
        return toString().replace("sonnicon.jade.", "");
    }

    Map<Object, Object> debugProperties();

    default Map<Object, Runnable> debugActions() {
        return null;
    }

    static String debugName(Object target) {
        if (target instanceof IDebuggable) {
            return ((IDebuggable) target).debugName();
        }

        return target.getClass().getName().replace("sonnicon.jade.", "") + "@" + Integer.toHexString(target.hashCode());
    }

    static Map<Object, Object> debugProperties(Object target) {
        if (target == null) {
            return new HashMap<>();
        }

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
        } else if (target instanceof Vector2) {
            Vector2 v = (Vector2) target;
            result.put("x", v.x);
            result.put("y", v.y);
        } else if (target instanceof Vector3) {
            Vector3 v = (Vector3) target;
            result.put("x", v.x);
            result.put("y", v.y);
            result.put("z", v.z);
        }

        return result;
    }
}
