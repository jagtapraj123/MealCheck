package com.example.meal_check.retrofit;

import com.example.meal_check.models.User;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiEndpointInterface {

    @POST("create_user")
    Call<User> createUser(@Body User user);

    @POST("get_user")
    Call<JsonObject> getUser(@Body JsonObject email);

    @POST("add_rating")
    Call<JsonObject> addRating(@Body JsonObject rating);

    @POST("get_suggestions")
    Call<JsonObject> getSuggestions(@Body JsonObject user);

    @POST("get_recipes")
    Call<JsonObject> getRecipes(@Body JsonObject name);

    @POST("set_prefs")
    Call<JsonObject> setPrefs(@Body JsonObject prefs);

    @POST("get_meal_plan")
    Call<JsonObject> getMealPlan(@Body JsonObject user);

}
