package main.src;

public class Node<T> {
    private T item;
    Node<T> next, last;

    public Node() {
        item = null;
        next = null;
        last = null;
    }

    public Node(T item) {
        this.item = item;
        next = null;
        last = null;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getLast() {
        return last;
    }

    public void setLast(Node<T> last) {
        this.last = last;
    }
}
