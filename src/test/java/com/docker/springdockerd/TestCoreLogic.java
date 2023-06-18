package com.docker.springdockerd;

public class TestCoreLogic {

    public static void main(String[] args) {

        int j = 4;
        int fact = 1;

        for (int i = j; i>0; i--){
            fact = fact * i;
        }

        System.out.println(fact(4));
    }

    public static long fact(long n){
        if(n<=1){
            return 1;
        } else {
            return n * fact(n-1);
        }
    }
}
