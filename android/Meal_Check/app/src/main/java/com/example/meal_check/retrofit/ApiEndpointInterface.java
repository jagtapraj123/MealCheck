package com.example.meal_check.retrofit;

import com.example.meal_check.models.User;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiEndpointInterface {

    @POST("create_user")
    Call<User> createUser(@Body User user);

    @GET("users")
    Call<User> getUser();

    @GET("users/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @GET("activity/")
    Call<JsonObject> boredActivity();


    @GET("timezone/{area}/{location}")
    Call<JsonObject> getTimezone(@Path("area") String area, @Path("location") String location);




}
