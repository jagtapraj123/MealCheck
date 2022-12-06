package com.example.meal_check.models;

public class User {
    //    email, name, height, weight, age, targetWeight fields
    private String email;
    private String name;
    private int height;
    private int weight;
    private int age;
    private int goal;
    private String activity_level;

    public User(String email, String name, int height, int weight, int age, int goal, String activity_level) {
        this.email = email;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.goal = goal;
        this.activity_level = activity_level;
    }
}