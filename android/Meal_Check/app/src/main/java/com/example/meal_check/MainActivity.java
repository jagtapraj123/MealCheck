package com.example.meal_check;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meal_check.adapters.RecipeAdapter;
import com.example.meal_check.databinding.ActivityMainBinding;
import com.example.meal_check.models.Recipe;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.HttpException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    FirebaseAuth mAuth;
    ApiEndpointInterface apiEndpointInterface;
    private ActivityMainBinding binding;
    private float[] lambda;
    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        lambda = new float[1];
        lambda[0] = 2.6f;

        findViewById(R.id.hello_world).setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        apiEndpointInterface = Api.getApi().create(ApiEndpointInterface.class);

        JsonObject jsonObject = new JsonObject();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL, "");
        jsonObject.addProperty("email", email);
        apiEndpointInterface.getUser(jsonObject).enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.code() == 403) {
                    Intent intent = new Intent(MainActivity.this, BoardingActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                } else if (response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body());
                    if (response.body().get("prefs") == null) {
                        startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
//                red color snackbar
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(binding.getRoot(), "Something went wrong...", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.RED);
                snackbar.show();

//                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        Dialog dialog = new Dialog(this);

        iniDialog(dialog);


        binding.getRecommendationsFab.setOnClickListener(view -> {
            ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
            InsetDrawable inset = new InsetDrawable(back, 20);
            dialog.getWindow().setBackgroundDrawable(inset);
            dialog.show();
//            startActivity(new Intent(this, RecommendationsActivity.class));
        });

        binding.signoutFab.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.setPreferencesFab.setOnClickListener(view -> {
            startActivity(new Intent(this, PreferencesActivity.class));
        });

        binding.addMealFab.setOnClickListener(view -> {
            Intent intent = new Intent(this, PreferencesActivity.class);
            intent.putExtra("addMeal", true);
            startActivity(intent);
        });

        recipes = new ArrayList<>();
        recipes.add(new Recipe(1, "Chicken Parmesan", "Chicken Parmesan is a dish of chicken cutlet, topped with tomato sauce and melted cheese, and baked in an oven. It is a variation of the Italian dish parmigiana di melanzane, which uses eggplant instead of chicken. It is a popular dish in the United States, where it is often served with spaghetti or linguine.",
                new ArrayList<String>(Arrays.asList("Chicken", "Parmesan", "Cheese", "Tomato Sauce", "Spaghetti", "Linguine")),
                "1. Preheat oven to 350 degrees F (175 degrees C). Lightly grease a 9x13 inch baking dish.\n" +
                        "2. In a shallow dish, combine the flour, salt, and pepper. In another shallow dish, beat the eggs. In a third shallow dish, combine the bread crumbs and Parmesan cheese. Dip chicken in flour mixture, then in egg, then in bread crumb mixture. Place chicken in prepared baking dish.\n" +
                        "3. Bake in preheated oven for 30 minutes. Remove from oven, and top each chicken breast with 1/4 cup tomato sauce and 1/4 cup mozzarella cheese. Return to oven, and bake until cheese is melted, about 10 minutes. An instant-read thermometer inserted into the center should read at least 165 degrees F (74 degrees C).",
                "30 minutes",
                "", 700.3));

        recipes.add(new Recipe(2, "Alfredo Pasta", "Alfredo sauce is a rich, creamy sauce made with butter, cream, and Parmesan cheese. It is named after Alfredo di Lelio, the owner of a restaurant in Rome where it was first served. It is often served with pasta, but can also be used as a sauce for chicken, fish, or vegetables.",
                new ArrayList<>(Collections.singletonList("Pasta, butter, cream, Parmesan cheese")),
                "1. Bring a large pot of lightly salted water to a boil. Add pasta and cook for 8 to 10 minutes or until al dente; drain.\n" +
                        "2. Melt butter in a saucepan over medium heat. Stir in flour until smooth, and cook for 1 minute. Gradually stir in cream, and cook over medium heat until thickened. Stir in Parmesan cheese, and season with salt and pepper. Stir in cooked pasta, and serve.",
                "30 minutes", "", 123));

        recipes.add(new Recipe(3, "Chicago Pizza", "Chicago-style pizza is a variation of deep-dish pizza, characterized by a thick crust and a sweet tomato sauce. It is often topped with Italian sausage, pepperoni, mushrooms, and green peppers. It is a popular dish in Chicago, where it was invented in the 1940s.",
                new ArrayList<>(Collections.singletonList("Pizza dough, tomato sauce, Italian sausage, pepperoni, mushrooms, green peppers, mozzarella cheese")),
                "1. Preheat oven to 425 degrees F (220 degrees C).\n" +
                        "2. Press dough into a 12 inch pizza pan. Spread pizza sauce over dough. Sprinkle with mozzarella cheese, Italian sausage, pepperoni, mushrooms, and green peppers. Sprinkle with Parmesan cheese.\n" +
                        "3. Bake in preheated oven for 25 to 30 minutes, or until crust is golden brown and cheese is bubbly.",
                "30 minutes", "", 500));


        RecipeAdapter adapter = new RecipeAdapter(recipes, this);
        binding.upcomingMealsRecyclerView.setAdapter(adapter);
        binding.upcomingMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailsActivity.class);
            intent.putExtra("recipe", recipes.get(position));
            startActivity(intent);
        });

//        get today's day
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

//        get monday, tuesday, wednesday, thursday, friday, saturday, sunday
        String[] days = new DateFormatSymbols().getWeekdays();
        String today = days[day];

        binding.day.setText(today);
        today = today.toLowerCase();

        Log.d(TAG, "onCreate: today: " + today);


        JsonObject mealPlanObject = new JsonObject();
        mealPlanObject.addProperty("email", email);
        mealPlanObject.addProperty("day", today);
        Log.d(TAG, "onCreate: email: " + email);


        Log.d(TAG, "onCreate: json: " + mealPlanObject.toString());

        Call<JsonObject> suggestions = apiEndpointInterface.getMealPlan(mealPlanObject);

        binding.linearLayout.setVisibility(View.GONE);
        binding.gifImageView.setVisibility(View.VISIBLE);
        binding.gettingMeals.setVisibility(View.VISIBLE);


        retryMealPlan(suggestions, adapter);


    }

    private void retryMealPlan(Call<JsonObject> suggestions, RecipeAdapter adapter) {
//
////        check if suggestions is enqueue
//        if (suggestions.isExecuted())
//            suggestions.cancel();
//
////        wait till suggestions is cancelled
//        while (suggestions.isExecuted() && !suggestions.isCanceled()) {
//            Log.d(TAG, "retryMealPlan: waiting");
//        }

        suggestions.clone()
                .enqueue(new retrofit2.Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {


                        if (response.code() == 404) {
                            Toast.makeText(MainActivity.this, "Be patient, generating plan...", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> retryMealPlan(suggestions, adapter), 5000);
                        } else {


                            binding.linearLayout.setVisibility(View.VISIBLE);
                            binding.gifImageView.setVisibility(View.GONE);
                            binding.gettingMeals.setVisibility(View.GONE);

                            JsonObject jsonObject = response.body();
                            Log.d(TAG, "onResponse: " + jsonObject);
                            recipes.clear();
                            if (jsonObject != null) {
                                for (int i = 0; i < jsonObject.get("meals").getAsJsonArray().size(); i++) {
                                    JsonObject recipeJson = jsonObject.get("meals").getAsJsonArray().get(i).getAsJsonObject();
                                    Gson gson = new Gson();
                                    Recipe recipe = gson.fromJson(recipeJson, Recipe.class);
                                    recipes.add(recipe);
                                }


                                adapter.updateRecipes(recipes);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

//                if 404 error, then retry
                        if (t instanceof HttpException) {
                            HttpException exception = (HttpException) t;
                            if (exception.code() == 404) {
                                new Handler().postDelayed(() -> retryMealPlan(suggestions, adapter), 5000);
                            }
                        } else {
                            binding.linearLayout.setVisibility(View.VISIBLE);
                            binding.gifImageView.setVisibility(View.GONE);
                            binding.gettingMeals.setVisibility(View.GONE);

                            Log.e(TAG, "onFailure: ", t);
                            Snackbar.make(binding.getRoot(), "Failed to get meals", Snackbar.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void iniDialog(Dialog dialog) {

//            show a dialog
        dialog.setContentView(R.layout.get_suggestions_dialog);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20, 0, 20, 0);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        Slider slider = dialog.findViewById(R.id.slider);
        slider.setValue(lambda[0]);
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            lambda[0] = (int) value;
        });


        dialog.findViewById(R.id.submit_text).setOnClickListener(v -> {
            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
            lambda[0] = slider.getValue();

            dialog.dismiss();

            Intent intent = new Intent(MainActivity.this, RecommendationsActivity.class);
            intent.putExtra("lambda", 5 - lambda[0]);
            startActivity(intent);
        });

        dialog.findViewById(R.id.cancel_text).setOnClickListener(v -> {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

    }
}