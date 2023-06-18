package com.viram.dev.controller;

public class MethodOver {
    public static void foo() {
        System.out.println("Test.foo() called ");
    }
    public void foo(int a) { // Compiler Error: cannot redefine foo()
        System.out.println("Test.foo(int) called ");
    }

    public static void main(String args[]) {
        MethodOver mo = new MethodOver();
        mo.foo(1);
    }
}
