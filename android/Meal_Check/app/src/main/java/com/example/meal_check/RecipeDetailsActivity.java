package com.example.meal_check;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.meal_check.databinding.ActivityRecipeDetailsBinding;
import com.example.meal_check.models.Recipe;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import retrofit2.Call;


public class RecipeDetailsActivity extends AppCompatActivity {

    private static final String TAG = "RecipeDetailsActivity";
    private ActivityRecipeDetailsBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        Toolbar toolbar = binding.toolbar;
//        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String email = user.getEmail();

        ApiEndpointInterface apiEndpointInterface = Api.getApi().create(ApiEndpointInterface.class);

        Intent intent = getIntent();
//        get recipe from intent
        Recipe recipe = (Recipe) intent.getSerializableExtra("recipe");
        Log.d(TAG, "onCreate: " + recipe);

        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        Snackbar.make(binding.getRoot(), recipe.getName(), Snackbar.LENGTH_LONG).show();
        toolBarLayout.setTitle(recipe.getName());

        if (binding.scrollLayout.recipeDescription != null) {
            binding.scrollLayout.recipeDescription.setText(recipe.getDescription());
        }
        if (binding.scrollLayout.ingredientsList != null) {

            Log.d(TAG, "onCreate: ingredients" + recipe.getIngredients());
//            convert array list to list
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipe.getIngredients());
            binding.scrollLayout.ingredientsList.setAdapter(itemsAdapter);
        }
        if (binding.scrollLayout.instructionsList != null) {
//            binding.scrollLayout.recipeInstructions.setText(recipe.getInstructions());
            String instructions = recipe.getInstructions();
//            split instructions by new line
            String[] instructionsArray = instructions.split("\\r?\\n");
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, instructionsArray);
            binding.scrollLayout.instructionsList.setAdapter(itemsAdapter);
        }
        if (binding.scrollLayout.prepTime != null) {
            String prepTime = recipe.getPrepTime() + " min";
            binding.scrollLayout.prepTime.setText(prepTime);
        }
        if (binding.scrollLayout.nutrition != null) {
            String nutrition = String.valueOf(recipe.getNutrition()) + " cal";
            binding.scrollLayout.nutrition.setText(nutrition);
        }
        if (recipe.getImage() != null) {
            Glide.with(this)
                    .load(recipe.getImage())
                    .into(binding.recipeImage);
        }

        Glide.with(this)
                .load(recipe.getImage())
                .into(binding.recipeImage);


        FloatingActionButton fab = binding.fab;
        final float[] rating = {5};

        Dialog dialog = new Dialog(this);

        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

//            show a dialog
            dialog.setContentView(R.layout.rating_dialog);
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 20, 0, 20, 0);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            Slider slider = dialog.findViewById(R.id.rating_slider);
            slider.setValue(rating[0]);

            dialog.findViewById(R.id.submit_text).setOnClickListener(v -> {
                Toast.makeText(this, "Rating submitted", Toast.LENGTH_SHORT).show();
                rating[0] = slider.getValue();

                JsonObject ratingObject = new JsonObject();
                ratingObject.addProperty("email", email);
                ratingObject.addProperty("recipe_id", recipe.getId());
                ratingObject.addProperty("rating", rating[0]);

                Call<JsonObject> call = apiEndpointInterface.addRating(ratingObject);
                call.enqueue(new retrofit2.Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view, "Rating submitted", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Snackbar.make(view, "Rating submission failed", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

                dialog.dismiss();
            });

            dialog.findViewById(R.id.cancel_text).setOnClickListener(v -> {
                Toast.makeText(this, "Rating cancelled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });


            dialog.show();
        });


    }
}