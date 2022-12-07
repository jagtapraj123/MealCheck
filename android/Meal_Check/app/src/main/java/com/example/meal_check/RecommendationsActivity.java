package com.example.meal_check;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.example.meal_check.adapters.RecipeAdapter;
import com.example.meal_check.databinding.ActivityRecommendationsBinding;
import com.example.meal_check.models.Recipe;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

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


        recipes = new ArrayList<>();
        recipes.add(new Recipe(1, "Chicken Parmesan", "Chicken Parmesan is a dish of chicken cutlet, topped with tomato sauce and melted cheese, and baked in an oven. It is a variation of the Italian dish parmigiana di melanzane, which uses eggplant instead of chicken. It is a popular dish in the United States, where it is often served with spaghetti or linguine.",
                "Chicken Parm)",
                new ArrayList<>(Arrays.asList("Chicken", "Parmesan", "Cheese", "Tomato Sauce", "Spaghetti", "Linguine")),
                "1. Preheat oven to 350 degrees F (175 degrees C). Lightly grease a 9x13 inch baking dish.\n" +
                        "2. In a shallow dish, combine the flour, salt, and pepper. In another shallow dish, beat the eggs. In a third shallow dish, combine the bread crumbs and Parmesan cheese. Dip chicken in flour mixture, then in egg, then in bread crumb mixture. Place chicken in prepared baking dish.\n" +
                        "3. Bake in preheated oven for 30 minutes. Remove from oven, and top each chicken breast with 1/4 cup tomato sauce and 1/4 cup mozzarella cheese. Return to oven, and bake until cheese is melted, about 10 minutes. An instant-read thermometer inserted into the center should read at least 165 degrees F (74 degrees C).",
                "30"));

        recipes.add(new Recipe(2, "Alfredo Pasta", "Alfredo sauce is a rich, creamy sauce made with butter, cream, and Parmesan cheese. It is named after Alfredo di Lelio, the owner of a restaurant in Rome where it was first served. It is often served with pasta, but can also be used as a sauce for chicken, fish, or vegetables.",
                "Alfredo Pasta", new ArrayList<>(Arrays.asList("Pasta", "Butter", "Cream", "Parmesan Cheese")), "1. Bring a large pot of lightly salted water to a boil. Add pasta and cook for 8 to 10 minutes or until al dente; drain.\n" +
                "1. Bring a large pot of lightly salted water to a boil. Add pasta and cook for 8 to 10 minutes or until al dente; drain.\n" +
                        "2. Melt butter in a saucepan over medium heat. Stir in flour until smooth, and cook for 1 minute. Gradually stir in cream, and cook over medium heat until thickened. Stir in Parmesan cheese, and season with salt and pepper. Stir in cooked pasta, and serve.",
                "30"));

        recipes.add(new Recipe(3, "Chicago Pizza", "Chicago-style pizza is a variation of deep-dish pizza, characterized by a thick crust and a sweet tomato sauce. It is often topped with Italian sausage, pepperoni, mushrooms, and green peppers. It is a popular dish in Chicago, where it was invented in the 1940s.",
                "Chicago Pizza",
                new ArrayList<>(Arrays.asList("Pizza", "Tomato Sauce", "Italian Sausage", "Pepperoni", "Mushrooms", "Green Peppers")),
                "1. Preheat oven to 425 degrees F (220 degrees C).\n" +
                        "2. Press dough into a 12 inch pizza pan. Spread pizza sauce over dough. Sprinkle with mozzarella cheese, Italian sausage, pepperoni, mushrooms, and green peppers. Sprinkle with Parmesan cheese.\n" +
                        "3. Bake in preheated oven for 25 to 30 minutes, or until crust is golden brown and cheese is bubbly.",
                "30"));


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



        getSuggestions(email);


    }

    private void getSuggestions(String email) {

        JsonObject jsonObject = new JsonObject();
        Log.d(TAG, "onCreate: email: " + email);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("lambda", 1);
        jsonObject.addProperty("count", 10);
        Log.d(TAG, "onCreate: json: " + jsonObject.toString());
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


                    RecipeAdapter adapter = (RecipeAdapter) binding.recyclerView.getAdapter();
                    assert adapter != null;
                    adapter.updateRecipes(recipes);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Snackbar.make(binding.getRoot(), "Failed to get recommendations", Snackbar.LENGTH_SHORT).show();


            }
        });

    }
}