package com.viram.dev.controller;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountString {
    public static void main(String[] args) {

        String name = "viram dhangar viram dhangar viram";
        //
        System.out.println(Arrays.stream(name.split(" ")).collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).toString());
    }
}
