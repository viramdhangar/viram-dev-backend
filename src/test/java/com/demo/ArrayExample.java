package com.demo;

import java.util.*;
import java.util.stream.*;

public class ArrayExample {

    Integer[][] intArra =  {{1,2,3}, {4,5,6}}; //transpose



    public static void main(String[] args) {
        Integer[] arr = {2, 3, 4};

        String[] strArray = {"2", "3", "4"};

        //id, name, price

        //Optional<Integer> value = Stream.of(arr).map(m -> m*m).reduce((a, b)->a+b);
        //System.out.println(value.get());

        //OptionalInt l = Stream.of(strArray).filter(f -> Integer.parseInt(f)%2==0).mapToInt(m -> Integer.parseInt(m)* Integer.parseInt(m)).reduce((a, b)->a+b);
        //System.out.println(l.getAsInt());

        List<Product> productList = Arrays.asList(
                new Product(1, "Mouse", 250),
                new Product(2, "Keyboard", 700),
                new Product(3, "Laptop", 35000),
                new Product(4, "HDMI Cable", 200),
                new Product(5, "Headphone", 1000));


        //Comparator<Product> nameSorting = productList.stream().sorted(productList, (p1,p2)->)

        //Collections.sort(productList, (p1, p2)-> p1.getName().compareTo(p2.getName()));
        //.out.println(productList);

        //productList.stream().sorted(Comparator.comparing(Product::getName)).forEach(System.out::println);

        //productList.stream().forEach(System.out::println);


        String nameString = "aabbcdceefh";
        String[] newStr = nameString.split("");
        //Stream.of(nameString.toCharArray()).

        Set<String> unique = new LinkedHashSet<>();
        //char string[]  = nameString.toCharArray();
        Set<String> duplicate = Stream.of(newStr).filter(f-> !unique.add(f)).collect(Collectors.toSet());

        unique.removeIf(r -> duplicate.contains(r));
        System.out.println(unique.stream().skip(2).findFirst().get());

       // System.out.println(unique);
        //unique.removeAll(duplicate);
        //System.out.println(unique);//d, f


        //unique.stream().skip(1).limit(1).forEach(System.out::println);

        //unique.stream().skip(1).findFirst().get();
        //System.out.println(unique.stream().skip(2).findFirst().get());


        // explore with linked list and other options
    }
}
