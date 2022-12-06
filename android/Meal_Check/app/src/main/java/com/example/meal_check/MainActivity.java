package com.example.meal_check;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.meal_check.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.hello_world).setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        binding.recipeCardBreakfast.recipeCard.setOnClickListener(view -> {
            startActivity(new Intent(this, RecipeDetailsActivity.class));
        });


        binding.signoutButton.setOnClickListener(view -> {
            // TODO : Signout
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        });
    }
}