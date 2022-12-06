package com.example.meal_check;

import android.content.Intent;
import android.os.Bundle;

import com.example.meal_check.databinding.ActivityRecipeDetailsBinding;
import com.example.meal_check.models.Recipe;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;


public class RecipeDetailsActivity extends AppCompatActivity {

    private ActivityRecipeDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Toolbar toolbar = binding.toolbar;
//        setSupportActionBar(toolbar);


        Intent intent = getIntent();
//        get recipe from intent
        Recipe recipe = (Recipe) intent.getSerializableExtra("recipe");
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        Snackbar.make(binding.getRoot(), recipe.getName(), Snackbar.LENGTH_LONG).show();
        toolBarLayout.setTitle(recipe.getName());

        if (binding.scrollLayout.recipeDescription != null) {
            binding.scrollLayout.recipeDescription.setText(recipe.getDescription());
        }
        if (binding.scrollLayout.recipeIngredients != null) {
            binding.scrollLayout.recipeIngredients.setText(recipe.getIngredients());
        }
        if (binding.scrollLayout.recipeInstructions != null) {
            binding.scrollLayout.recipeInstructions.setText(recipe.getInstructions());
        }
        if (binding.scrollLayout.prepTime != null) {
            binding.scrollLayout.prepTime.setText(recipe.getPrepTime());
        }


        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}