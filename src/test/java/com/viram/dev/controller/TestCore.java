package com.viram.dev.controller;

import java.util.Arrays;
import java.util.List;

public class TestCore {
    private final List<Integer> list = Arrays.asList(1,2,3,4,5);

    public boolean useList(){
        //list.remove(2);

        list.stream().map(m -> m+2).forEach(System.out::print);
        System.out.println(list);
        return false;
    }

    public static void main(String[] args) {
       A a  = new A();

       B b =  new B();
       b.dos();
    }
}
class A {

    private void dos(){
        System.out.println("private method");
    }
    protected void dos1(double a,double b){

    }

}


class B extends A{

    public void dos(){
        System.out.println("private method in B");
    }

}
