package com.demo;

public class QueExample {

    //implement queue,
    // two method to pull and push data in to queue

    private Node firstNode;
    private Node lastNode;


    public void push(String str){
        if(firstNode == null){
            Node node = new Node(str, lastNode);
            this.firstNode = node;
            this.lastNode = node;
        } else {
            Node node = new Node(str, lastNode);
            this.lastNode = node;
        }
    }
    // to be continue...
    // regular expression
}
