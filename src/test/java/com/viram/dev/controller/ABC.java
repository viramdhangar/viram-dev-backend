package com.viram.dev.controller;

import com.viram.dev.dto.Profile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ABC {
    public static void main(String[] args) {

        List<Integer> intList=new ArrayList<Integer>();
        intList.add(2);
        intList.add(3);
        intList.add(5);
        intList.add(2);
        intList.add(2);
        intList.add(3);
        intList.add(10);




        /*Set<Integer> unique = new HashSet<>();


        Set<Integer> duplicate = intList.stream().filter(f-> !unique.add(f)).collect(Collectors.toSet());

        System.out.println(unique.toString());
        System.out.println(duplicate.toString());
        unique.removeAll(duplicate);
        System.out.println(unique.toString());

        Map<Integer, Long> map =  intList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        System.out.println(map.toString());

        Optional<Integer> is = intList.stream().sorted(Comparator.reverseOrder()).findFirst();
        if(is.isPresent()){
            System.out.println(is.get());
        }

        int num1 = -8;
        int num2 = 15;

        num1 = num1-num2;
        num2 = num1+num2;
        num1 = num2-num1;

        System.out.println(num1+", "+num2);

        int rows = 10;

        // loop to iterate for the given number of rows
        for (int i = 1; i <= rows; i++) {
            // loop to print the number of spaces before the star
            for (int j = rows; j >= i; j--) {
                System.out.print(" ");
            }
            // loop to print the number of stars in each row
            for (int j = 1; j <= i; j++) {
                System.out.print("* ");
            }
            // for new line after printing each row
            System.out.println();
        }*/

        List<Integer> collect = intList.stream().filter(f -> f > 2).map(m-> m*10).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println(collect.toString());
    }

}
