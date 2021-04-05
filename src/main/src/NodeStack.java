package main.src;

import main.interfaces.IStack;

public class NodeStack<T> implements IStack<T> {
    private Node<T> top;

    public NodeStack() {
        top = null;
    }

    public NodeStack(T item) {
        this.top = new Node<>(item);
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        if (top != null) {
            top.setNext(newNode);
            newNode.setLast(top);
        }
        top = newNode;
    }

    @Override
    public T pop() {
        if (top != null) {
            Node<T> temp = top;
            if (top.getLast() != null) {
                top = temp.getLast();
                temp.setLast(null);
                top.setNext(null);
            } else top = null;
            return temp.getItem();
        } return null;
    }

    @Override
    public T peek() {
        return top.getItem();
    }
}
