package com.demo;

public class SingletonClassExam {

    private static SingletonClassExam instance; //volatile

    private SingletonClassExam(){};

    public static synchronized SingletonClassExam getInstance(){

        if(instance == null){
            synchronized (instance) {
                instance = new SingletonClassExam();
            }
        }

        return instance;
    }
}
