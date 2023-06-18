package com.docker.springdockerd;

public class TestProfileBuilder {
    public static void main(String[] args) {

        Profile p = new Profile.ProfileBuilder(1).setAddress("dsdsd").setName("Viram").build();
        System.out.println(p.getAddress()+ "   " +p.getName());

    }
}
