package sonnicon.jade.util;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjectPool {
    private static final HashMap<Class<? extends IPooledObject>, ArrayList<Object>> STORE = new HashMap<>();

    public static <T extends IPooledObject> T obtain(Class<T> type) {
        ArrayList<Object> list = STORE.get(type);
        T obj;
        if (list == null || list.isEmpty()) {
            try {
                obj = type.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            obj = (T) list.remove(list.size() - 1);
        }
        obj.onObtained();
        return obj;
    }

    public static void free(IPooledObject obj) {
        Class<? extends IPooledObject> clazz = obj.getClass();
        ArrayList<Object> list = STORE.computeIfAbsent(clazz, k -> new ArrayList<>());
        list.add(obj);
        obj.onFree();
    }

    public interface IPooledObject {
        default void onObtained() {

        }

        default void onFree() {

        }
    }
}
