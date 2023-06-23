package sonnicon.jade.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DoubleLinkedList<T> implements List<T> {
    protected DoubleLinkedListNode<T> head, tail;
    protected int length = 0;

    @Override
    public int size() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    public DoubleLinkedListNode<T> findNode(Object o) {
        for (DoubleLinkedListNode<T> node = head; node != null; node = node.next) {
            if (node.value == o) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean contains(Object o) {
        return findNode(o) != null;
    }

    @Override
    public Iterator<T> iterator() {
        return new DoubleLinkedListIterator<>(this);
    }

    @Override
    public Object[] toArray() {
        int size = size();
        T[] array = (T[]) new Object[size];
        DoubleLinkedListNode<T> node = head;
        for (int i = 0; i < size; i++) {
            array[i] = node.value;
            node = node.next;
        }
        return array;
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return (T1[]) toArray();
    }

    @Override
    public boolean add(T t) {
        return addNode(new DoubleLinkedListNode<>(t));
    }

    public boolean addNode(DoubleLinkedListNode<T> node) {
        if (node == null) {
            return false;
        }

        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        length++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return removeNode(findNode(o));
    }

    public boolean removeNode(DoubleLinkedListNode<T> node) {
        if (node == null) {
            return false;
        }

        if (node == head) {
            head = node.next;
        }
        if (node == tail) {
            tail = node.prev;
        }
        node.prev.next = node.next;
        node.next.prev = node.prev;
        length--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object o : collection) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        collection.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        for (T element : collection) {
            add(i++, element);
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        collection.forEach(this::remove);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        for (DoubleLinkedListNode<T> node = head; node != null; node = node.next) {
            if (!collection.contains(node.value)) {
                removeNode(node);
            }
        }
        return true;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        length = 0;
    }

    @Override
    public T get(int i) {
        return getNode(i).value;
    }

    public DoubleLinkedListNode<T> getNode(int i) {
        DoubleLinkedListNode<T> result = head;
        while (i > 0) {
            result = result.next;
            i--;
        }
        return result;
    }

    @Override
    public T set(int i, T t) {
        getNode(i).value = t;
        return t;
    }

    public DoubleLinkedListNode<T> setNode(int i, DoubleLinkedListNode<T> t) {
        DoubleLinkedListNode<T> old = getNode(i);
        if (old == head) {
            head = t;
        } else {
            old.prev.next = t;
            t.prev = old.prev;
        }

        if (old == tail) {
            tail = t;
        } else {
            old.next.prev = t;
            t.next = old.next;
        }
        return t;
    }

    @Override
    public void add(int i, T t) {
        addNode(i, new DoubleLinkedListNode<>(t));
    }

    public void addNode(int i, DoubleLinkedListNode<T> t) {
        if (i == length) {
            addNode(t);
        } else if (i == 0) {
            t.next = head;
            head = t;
            length++;
        } else {
            DoubleLinkedListNode<T> after = getNode(i);
            t.next = after;
            t.prev = after.prev;
            after.prev.next = t;
            after.prev = t;
            length++;
        }
    }

    @Override
    public T remove(int i) {
        return removeNode(i).value;
    }

    public DoubleLinkedListNode<T> removeNode(int i) {
        DoubleLinkedListNode<T> node = getNode(i);
        if (removeNode(node)) {
            return node;
        } else {
            return null;
        }
    }

    @Override
    public int indexOf(Object o) {
        int size = size();
        DoubleLinkedListNode<T> node = head;
        for (int i = 0; i < size; i++) {
            if (node.value == o) {
                return i;
            }
            node = node.next;
        }
        return -1;
    }

    public int indexOfNode(DoubleLinkedListNode<T> node) {
        int size = size();
        for (int i = 0; i < size; i++) {
            if (node == head) {
                return i;
            }
            node = node.prev;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        DoubleLinkedListNode<T> node = head;
        for (int i = size() - 1; i >= 0; i--) {
            if (node.value == o) {
                return i;
            }
            node = node.next;
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DoubleLinkedListListIterator<T>(this);
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return new DoubleLinkedListListIterator<>(this, i);
    }

    public DoubleLinkedListNodeIterator<T> nodeIterator() {
        return new DoubleLinkedListNodeIterator<>(this);
    }

    @Override
    public List<T> subList(int i, int i1) {
        //todo think of a way to make this work nicely
        throw new UnsupportedOperationException();
    }


    public static class DoubleLinkedListNode<K> {
        protected DoubleLinkedListNode<K> prev, next;
        public K value;

        public DoubleLinkedListNode() {

        }

        public DoubleLinkedListNode(K value) {
            this.value = value;
        }
    }

    public static class DoubleLinkedListIterator<K> implements Iterator<K> {
        protected DoubleLinkedList<K> list;
        protected DoubleLinkedListNode<K> node;

        public DoubleLinkedListIterator(DoubleLinkedList<K> doubleLinkedList) {
            this.list = doubleLinkedList;
            this.node = list.head;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public K next() {
            K result = node.value;
            node = node.next;
            return result;
        }

        @Override
        public void remove() {
            list.removeNode(node.prev);
        }
    }

    public static class DoubleLinkedListListIterator<K> extends DoubleLinkedListIterator<K> implements ListIterator<K> {
        public DoubleLinkedListListIterator(DoubleLinkedList<K> doubleLinkedList) {
            super(doubleLinkedList);
        }

        public DoubleLinkedListListIterator(DoubleLinkedList<K> doubleLinkedList, int index) {
            super(doubleLinkedList);
            node = list.getNode(index);
        }

        @Override
        public boolean hasPrevious() {
            return node.prev != null;
        }

        @Override
        public K previous() {
            return (node = node.prev).value;
        }

        @Override
        public int nextIndex() {
            return list.indexOfNode(node);
        }

        @Override
        public int previousIndex() {
            return list.indexOfNode(node.prev);
        }

        @Override
        public void set(K k) {
            node.value = k;
        }

        @Override
        public void add(K k) {
            list.add(k);
        }
    }

    public static class DoubleLinkedListNodeIterator<K> implements Iterator<DoubleLinkedListNode<K>> {
        protected DoubleLinkedList<K> list;
        protected DoubleLinkedListNode<K> node;

        public DoubleLinkedListNodeIterator(DoubleLinkedList<K> doubleLinkedList) {
            this.list = doubleLinkedList;
            this.node = list.head;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public DoubleLinkedListNode<K> next() {
            DoubleLinkedListNode<K>  result = node;
            node = node.next;
            return result;
        }

        @Override
        public void remove() {
            list.removeNode(node.prev);
        }
    }
}