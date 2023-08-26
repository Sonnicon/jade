package sonnicon.jade.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Structs {
    public static <T> HashSet<T> setFrom(T... objs) {
        return (HashSet<T>) Arrays.stream(objs).collect(Collectors.toSet());
    }

    public static <T> HashSet<T> setExtendFrom(HashSet<T> set, T... objs) {
        set.addAll(Arrays.asList(objs));
        return set;
    }

    public static <T, K> HashMap<T, K> mapFrom(Object... objs) {
        HashMap<T, K> result = new HashMap<>();
        for (int i = 0; i < objs.length; i += 2) {
            result.put((T) objs[i], (K) objs[i + 1]);
        }
        return result;
    }

    public static <T, K> Map<T, K> mapExtendFrom(Map<T, K> map, Object... objs) {
        for (int i = 0; i < objs.length; i += 2) {
            map.put((T) objs[i], (K) objs[i + 1]);
        }
        return map;
    }
}
