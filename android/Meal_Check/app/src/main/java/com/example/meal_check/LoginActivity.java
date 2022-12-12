package com.example.meal_check;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.meal_check.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.EMAIL, Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        binding.signupButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });


        binding.loginButton.setOnClickListener(view -> {
            // TODO : Login

            if (Objects.requireNonNull(binding.email.getText()).toString().isEmpty() || Objects.requireNonNull(binding.password.getText()).toString().isEmpty()) {
                binding.email.setError("Please enter an email");
                binding.password.setError("Please enter a password");
            } else {
                mAuth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        binding.email.setError("Invalid email or password");
                        binding.password.setError("Invalid email or password");
                    }
                });
            }

        });
    }
}