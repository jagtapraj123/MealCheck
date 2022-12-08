package com.example.meal_check.retrofit;

import com.example.meal_check.models.Recipe;
import com.example.meal_check.models.User;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiEndpointInterface {

    @POST("create_user")
    Call<User> createUser(@Body User user);

    @POST("add_rating")
    Call<JsonObject> addRating(@Body JsonObject rating);

    @POST("get_suggestions")
    Call<JsonObject> getSuggestions(@Body JsonObject user);

    @GET("get_all_recipes")
    Call<ArrayList<Recipe>> getAllRecipes();

    @POST("get_recipes")
    Call<JsonObject> getRecipes(@Body JsonObject name);

    @POST("set_prefs")
    Call<JsonObject> setPrefs(@Body JsonObject prefs);


}
