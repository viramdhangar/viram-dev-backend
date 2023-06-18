package com.viram.dev.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatMapExample {
    public static void main(String[] args) {
        List<String> list1 = new ArrayList<>();

        list1.add("one");
        list1.add("two");
        list1.add("three");
        list1.add("four");
        list1.add("five");

        // creating list 2
        List<String> list2 = new ArrayList<>();
        list1.add("five");
        list2.add("six");
        list2.add("seven");
        list2.add("eight");
        list2.add("nine");
        list2.add("ten");


        System.out.println("List1 values : "+list1);
        System.out.println("List2 values : "+list2);

        Stream.of(list1, list2).flatMap(list -> list.stream().distinct()).forEach(System.out::println);


    }
}
