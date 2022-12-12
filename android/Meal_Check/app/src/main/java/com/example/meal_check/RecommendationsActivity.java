package com.example.meal_check;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meal_check.adapters.RecipeAdapter;
import com.example.meal_check.databinding.ActivityRecommendationsBinding;
import com.example.meal_check.models.Recipe;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;

public class RecommendationsActivity extends AppCompatActivity {

    private static final String TAG = "RecommendationsActivity";
    ActivityRecommendationsBinding binding;
    ApiEndpointInterface apiEndpointInterface;
    ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        String email = preferences.getString(Constants.EMAIL, "");

        apiEndpointInterface = Api.getApi().create(ApiEndpointInterface.class);

        binding.loadingImage.setVisibility(View.VISIBLE);
        binding.loadingText.setVisibility(View.VISIBLE);

        recipes = new ArrayList<>();

        RecipeAdapter recipeAdapter = new RecipeAdapter(recipes, RecommendationsActivity.this);

        RecipeAdapter.OnItemClickListener listener = position -> {
            Recipe recipe = recipes.get(position);
            Intent intent = new Intent(RecommendationsActivity.this, RecipeDetailsActivity.class);
            intent.putExtra("recipe", recipe);
            startActivity(intent);
        };

        recipeAdapter.setOnItemClickListener(listener);
        binding.recyclerView.setAdapter(recipeAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(RecommendationsActivity.this));


        float lambda = getIntent().getFloatExtra("lambda", 1);

        getSuggestions(email, lambda);


    }

    private void getSuggestions(String email, float lambda) {

        JsonObject jsonObject = new JsonObject();
        Log.d(TAG, "onCreate: email: " + email);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("lambda", lambda);
        jsonObject.addProperty("count", 10);
        Log.d(TAG, "onCreate: json: " + jsonObject);
        Call<JsonObject> suggestions = apiEndpointInterface.getSuggestions(jsonObject);
        suggestions.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                Log.d(TAG, "onResponse: " + jsonObject);
                if (jsonObject != null) {
                    for (int i = 0; i < jsonObject.get("suggestions").getAsJsonArray().size(); i++) {
                        JsonObject recipeJson = jsonObject.get("suggestions").getAsJsonArray().get(i).getAsJsonObject();
                        Gson gson = new Gson();
                        Recipe recipe = gson.fromJson(recipeJson, Recipe.class);
                        recipes.add(recipe);
                    }
                    if (recipes.size() == 0) {
                        binding.noRecommendationsTextview.setVisibility(View.VISIBLE);
                    } else binding.noRecommendationsTextview.setVisibility(View.GONE);


                    RecipeAdapter adapter = (RecipeAdapter) binding.recyclerView.getAdapter();
                    assert adapter != null;
                    adapter.updateRecipes(recipes);
                    binding.loadingImage.setVisibility(View.GONE);
                    binding.loadingText.setVisibility(View.GONE);
                } else {
                    binding.loadingImage.setVisibility(View.GONE);
                    binding.loadingText.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), "Something went wrong, please try after some time", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Snackbar.make(binding.getRoot(), "Failed to get recommendations", Snackbar.LENGTH_SHORT).show();
                binding.loadingImage.setVisibility(View.GONE);
                binding.loadingText.setVisibility(View.GONE);
                finish();
            }


        });

    }
}