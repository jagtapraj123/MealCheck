package com.example.meal_check.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    public static final String BASE_URL = "http://10.0.0.107:8000/";
    public static Retrofit retrofit = null;

    public static Retrofit getApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(82, TimeUnit.SECONDS)
                .readTimeout(82, TimeUnit.SECONDS).build();

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
