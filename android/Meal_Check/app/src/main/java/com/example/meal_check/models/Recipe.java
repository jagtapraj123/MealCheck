package com.example.meal_check.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    @SerializedName("recipe_id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("image")
    private String image;
    @SerializedName("ingredients")
    private ArrayList<String> ingredients;
    @SerializedName("steps")
    private String instructions;
    @SerializedName("minutes")
    private String prepTime;
    @SerializedName("nutrition")
    private double nutrition;


    public Recipe(int id, String name, String description, ArrayList<String> ingredients, String instructions, String prepTime, String image, double nutrition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.prepTime = prepTime;
        this.nutrition = nutrition;
    }
//
//    public Recipe(int id, String name, String description, ArrayList<String> ingredients, String instructions, String prepTime, double nutrition) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.ingredients = ingredients;
//        this.instructions = instructions;
//        this.prepTime = prepTime;
//        this.nutrition = nutrition;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public double getNutrition() {
        return nutrition;
    }

    public void setNutrition(double nutrition) {
        this.nutrition = nutrition;
    }
}
