package sonnicon.jade.util;

import java.util.*;

public class SortedTripletList<A extends Comparable<A>, B, C>{
    public final ArrayList<A> as = new ArrayList<>();
    private final ArrayList<B> bs = new ArrayList<>();
    private final ArrayList<C> cs = new ArrayList<>();

    public int size() {
        return as.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsA(A a) {
        return as.contains(a);
    }

    public boolean containsB(B b) {
        return bs.contains(b);
    }

    public boolean containsC(C c) {
        return cs.contains(c);
    }

    public boolean add(A a, B b, C c) {
        //todo not linear
        int i = 0;
        for (; i < as.size(); i++) {
            if (as.get(i).compareTo(a) > 0) break;
        }
        as.add(i, a);
        bs.add(i, b);
        cs.add(i, c);
        return true;
    }

    public boolean remove(A a, B b, C c) {
        for (int i = as.indexOf(a); as.get(i).compareTo(a) == 0; i++) {
            if (bs.get(i) == b && cs.get(i) == c) {
                as.remove(i);
                bs.remove(i);
                cs.remove(i);
                return true;
            }
        }
        return false;
    }

    public void clear() {
        as.clear();
        bs.clear();
        cs.clear();
    }

    public A getAIndex(int i) {
        return as.get(i);
    }

    public B getBIndex(int i) {
        return bs.get(i);
    }

    public C getCIndex(int i) {
        return cs.get(i);
    }

    public void setIndex(int i, A a, B b, C c) {
        as.set(i, a);
        bs.set(i, b);
        cs.set(i, c);
    }

    public void removeIndex(int i) {
        as.remove(i);
        bs.remove(i);
        cs.remove(i);
    }

    public int indexOfA(A a) {
        return as.indexOf(a);
    }

    public int indexOfB(B b) {
        return bs.indexOf(b);
    }

    public int indexOfC(C c) {
        return cs.indexOf(c);
    }
}
