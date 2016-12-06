package ppke.itk.xplang.util;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Stack<T> {
    private Deque<T> list = new LinkedList<T>();

    public Stack() {}

    public Stack(T initialValue) {
        list.add(initialValue);
    }

    public Stack(Collection<T> initialValues) {
        list.addAll(initialValues);
    }

    public void push(T value) {
        list.add(value);
    }

    public void push(Collection<T> values) {
        list.addAll(values);
    }

    public T peek() throws NoSuchElementException {
        if(list.isEmpty()) throw new NoSuchElementException("Tried to peek empty stack");
        return list.peekLast();
    }

    public T pop() throws NoSuchElementException {
        if(list.isEmpty()) throw new NoSuchElementException("Tried to pop from empty stack");
        return list.pollLast();
    }

    /**
     * Pop a value, but only if it's of {@code expect} type, otherwise throw an exception.
     * @param expect The expected class of the element
     * @param <U> The type of expected element.
     * @return The top value of the stack, if its type is {@code expect}.
     * @throws ClassCastException If the top value of the stack is not a subclass of {@code expect}.
     */
    public <U extends T> U pop(Class<U> expect) throws ClassCastException {
        T val = pop();
        return expect.cast(val);
    }

    /**
     * Pop and discard the top {@code n} elements. If there are less than {@code n} elements in the stack, the stack is
     * just emptied.
     * @param n The number of elements to discard
     */
    public void drop(int n) {
        while(n-->0 && !list.isEmpty()) {
            list.removeLast();
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override public String toString() {
        return list.toString();
    }
}
