package model;

public class Node {
    Task task;
    Node prev;
    Node next;

    Node(Node prev, Task task, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}