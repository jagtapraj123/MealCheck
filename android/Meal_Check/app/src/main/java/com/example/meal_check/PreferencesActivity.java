package com.example.meal_check;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meal_check.adapters.SearchAdapter;
import com.example.meal_check.databinding.ActivityPreferencesBinding;
import com.example.meal_check.models.Recipe;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferencesActivity extends AppCompatActivity {

    private final String TAG = "PreferencesActivity";
    ArrayList<Recipe> recipes;
    SearchAdapter searchAdapter;
    ApiEndpointInterface apiEndpointInterface;
    ArrayList<Integer> ids;
    ActivityPreferencesBinding binding;
    int limit = 10;
    boolean addMeal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiEndpointInterface = Api.getApi().create(ApiEndpointInterface.class);

        recipes = new ArrayList<>();

        addMeal = getIntent().getBooleanExtra("addMeal", false);

        if (addMeal) {
            binding.searchInputLayout.setHint("Search for a meal to add");
            binding.submitPreferences.setText("Add Meal");
            limit = 1;
        }


        binding.submitPreferences.setOnClickListener(view -> {

            if (ids == null || ids.size() == 0) {
                if (addMeal) {
                    Toast.makeText(this, "Please select a meal to add", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show();
                }
            } else if (ids.size() < limit) {
                int remaining = limit - ids.size();
                Toast.makeText(this, "Please select at least " + remaining + " more recipes", Toast.LENGTH_SHORT).show();
            } else if (addMeal) {
                if (ids.size() != 1) {
                    Toast.makeText(this, "Please select only one meal", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("recipe_id", ids.get(0));
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
                    String email = sharedPreferences.getString(Constants.EMAIL, "");
                    jsonObject.addProperty("email", email);
                    jsonObject.addProperty("rating", 5);
                    apiEndpointInterface.addRating(jsonObject).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PreferencesActivity.this, "Meal added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(PreferencesActivity.this, "Error adding meal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(PreferencesActivity.this, "Error adding meal", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                JsonObject jsonObject = new JsonObject();
                JsonArray jsonArray = new JsonArray();
                for (int i = 0; i < ids.size(); i++) {
                    jsonArray.add(ids.get(i));
                }
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
                String email = sharedPreferences.getString(Constants.EMAIL, "");
                jsonObject.addProperty("email", email);
                jsonObject.add("prefs", jsonArray);

                Log.d(TAG, "onClick: " + jsonObject);
                apiEndpointInterface.setPrefs(jsonObject).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PreferencesActivity.this, "Preferences set successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(PreferencesActivity.this, "Error setting preferences", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(PreferencesActivity.this, "Error setting preferences", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        RecyclerView recyclerView = findViewById(R.id.rvPreferences);


        searchAdapter = new SearchAdapter(this, recipes, position -> {
            if (ids == null)
                ids = new ArrayList<>();
            if (ids.contains(recipes.get(position).getId())) {
                ids.remove(Integer.valueOf(recipes.get(position).getId()));
            } else {
                if (addMeal) {
                    if (ids.size() == 1) {
                        Toast.makeText(this, "You can select only one meal at a time", Toast.LENGTH_SHORT).show();
                    } else {
                        ids.add(recipes.get(position).getId());
                    }
                } else {
                    ids.add(recipes.get(position).getId());
                }
            }
            searchAdapter.updateIds(ids);
        });

        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        binding.searchInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String query = editable.toString();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("string", query);
                apiEndpointInterface.getRecipes(jsonObject).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JsonObject jsonObject = response.body();

                            JsonArray jsonArray = jsonObject.getAsJsonArray("recipes");

                            Log.d(TAG, "onResponse: " + jsonArray.toString());
                            recipes = new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject recipe = jsonArray.get(i).getAsJsonObject();
                                int id = recipe.get("recipe_id").getAsInt();
                                String name = recipe.get("name").getAsString();
                                String description = recipe.get("description").getAsString();

                                String image;
                                if (recipe.get("image").isJsonNull())
                                    image = "";
                                else
                                    image = recipe.get("image").getAsString();

                                ArrayList<String> ingredients = new ArrayList<>();
                                String instructions = recipe.get("steps").getAsString();
                                String time = recipe.get("minutes").getAsString();
                                double nutrition = recipe.get("nutrition").getAsDouble();

                                recipes.add(new Recipe(id, name, description, ingredients, instructions, time, image, nutrition));
//                                recipes.add(new Recipe(recipe.get("recipe_id").getAsInt(), recipe.get("name").getAsString(), recipe.get("description").getAsString(), new ArrayList<>(), recipe.get("steps").getAsString(), recipe.get("minutes").getAsString(), recipe.get("image").getAsString(), recipe.get("nutrition").getAsDouble()));
                            }
                            searchAdapter.updateRecipes(recipes);
                        } else {
                            Toast.makeText(PreferencesActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(PreferencesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}