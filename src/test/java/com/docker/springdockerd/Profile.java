package com.docker.springdockerd;

import lombok.Getter;


@Getter
public class Profile {
    private int id;
    private String name;
    private String address;

    public Profile(ProfileBuilder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.address = builder.address;
    }

    public static class ProfileBuilder{

        private int id;
        private String name;
        private String address;

        public ProfileBuilder(int id) {
            this.id = id;
        }

        public ProfileBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ProfileBuilder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Profile build(){return new Profile(this);}
    }

}
