package com.viram.dev.controller;

import com.docker.springdockerd.Profile;

import java.math.BigDecimal;
import java.util.*;

public class TestComparator {
    public static void main(String[] args) {
        Profile p1 = new Profile.ProfileBuilder(1).setAddress("A").setName("Viram").build();
        Profile p2 = new Profile.ProfileBuilder(1).setAddress("B").setName("Prem").build();
        Profile p3 = new Profile.ProfileBuilder(1).setAddress("B").setName("Zahul").build();

        List<Profile> profileList = Arrays.asList(p1, p2, p3);

        List<Integer> intList = Arrays.asList(1,2,3,4,4,5,6,6);

        Set<Integer> sets = new HashSet<>();
        //intList.removeIf(p -> !sets.add(p));


        //intList.stream().forEach(System.out::println);

        //profileList.stream().map(m->m.getAddress()).distinct().forEach(f-> System.out.println(f));

        /*Collections.sort(profileList, (s1, s2)-> s2.getAddress().compareTo(s1.getAddress()));
        for (Profile profile : profileList) {
            System.out.println(profile.getAddress());
        }*/
       // profileList.stream().sorted(Comparator.comparing(Profile::getName).reversed()).forEach(p->System.out.println(p.getAddress()));
        BigDecimal bd1= new BigDecimal(2);
        BigDecimal bd2= new BigDecimal(5.7);
        System.out.println(get(bd1, bd2));

    }

    public static Number get(Number n1, Number n2){
        return n1.doubleValue() + n2.doubleValue();
    }
}
