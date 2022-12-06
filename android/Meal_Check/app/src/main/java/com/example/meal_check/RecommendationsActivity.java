package com.example.meal_check;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.meal_check.adapters.RecipeAdapter;
import com.example.meal_check.databinding.ActivityRecommendationsBinding;
import com.example.meal_check.models.Recipe;

import java.io.Serializable;
import java.util.ArrayList;

public class RecommendationsActivity extends AppCompatActivity {

    ActivityRecommendationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe(1, "Alfredo Pasta", "THis is the description for the pasta",
                "www.image.com", "pasta, cheese, butter", "cook pasta, add butter, add cheese", "30 minutes"));

        recipes.add(new Recipe(2, "Chicken", "This is the description for the chicken",
                "www.image.com", "chicken, butter, salt", "cook chicken, add butter, add salt", "30 minutes"));

        recipes.add(new Recipe(3, "Noodles", "This is the description for the noodles",
                "www.image.com", "noodles, butter, salt", "cook noodles, add butter, add salt", "30 minutes"));

        recipes.add(new Recipe(4, "Rice", "This is the description for the rice",
                "www.image.com", "rice, butter, salt", "cook rice, add butter, add salt", "30 minutes"));

        recipes.add(new Recipe(5, "Steak", "This is the description for the steak",
                "www.image.com", "steak, butter, salt", "cook steak, add butter, add salt", "30 minutes"));


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecipeAdapter.OnItemClickListener listener = new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Recipe recipe = recipes.get(position);
                Intent intent = new Intent(RecommendationsActivity.this, RecipeDetailsActivity.class);
                intent.putExtra("recipe", (Serializable) recipe);
                startActivity(intent);
            }

        };

        RecipeAdapter recipeAdapter = new RecipeAdapter(recipes, this, listener);
        binding.recyclerView.setAdapter(recipeAdapter);


    }
}