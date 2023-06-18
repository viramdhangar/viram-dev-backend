package com.docker.springdockerd;

public class TryClass {

    public static void main(String[] args) {

        int i = 0;
        Integer j = null;


        //System.out.println(j++);

        A a = new A();
        a.add();

        A b = new B();
        b.add();

        B c = new B();
        c.add();

    }

}

class A{

    public static void add(){
        System.out.println("A");
    }
}

class B extends A {

    public static void add(){
        System.out.println("B");
    }
}
