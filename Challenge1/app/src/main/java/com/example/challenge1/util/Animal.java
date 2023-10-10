package com.example.challenge1.util;

// Define a class called "Animal"
public class Animal {

    private final int avatar;
    private String owner;
    private String name;
    private int age;

    public Animal(int avatar, String owner,String name, int age) {
        this.avatar = avatar;
        this.owner = owner;
        this.name = name;
        this.age = age;
    }
    public int getAvatar() {
        return avatar;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
