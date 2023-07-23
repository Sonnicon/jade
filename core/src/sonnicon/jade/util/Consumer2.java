package sonnicon.jade.util;

@FunctionalInterface
public interface Consumer2<A, B> {
    void apply(A a, B b);
}
