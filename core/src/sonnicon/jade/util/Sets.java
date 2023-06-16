package sonnicon.jade.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Sets {
    public static <T> HashSet<T> from(Object... objs) {
        return (HashSet<T>) Arrays.stream(objs).collect(Collectors.toSet());
    }
}
