package com.example.meal_check;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meal_check.databinding.ActivityBoardingBinding;
import com.example.meal_check.models.User;
import com.example.meal_check.retrofit.Api;
import com.example.meal_check.retrofit.ApiEndpointInterface;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardingActivity extends AppCompatActivity {

    private static final String TAG = "BoardingActivity";
    ActivityBoardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ApiEndpointInterface apiService = Api.getApi().create(ApiEndpointInterface.class);

        String email = getIntent().getStringExtra("email");
        binding.email.setText(email);

        binding.submit.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.name.getText()).toString().isEmpty())
                binding.name.setError("Name is required");
            else if (Objects.requireNonNull(binding.height.getText()).toString().isEmpty())
                binding.height.setError("Height is required");
            else if (Objects.requireNonNull(binding.weight.getText()).toString().isEmpty())
                binding.weight.setError("Weight is required");
            else if (Objects.requireNonNull(binding.age.getText()).toString().isEmpty())
                binding.age.setError("Age is required");
            else if (Objects.requireNonNull(binding.goal.getText()).toString().isEmpty())
                binding.goal.setError("Target weight is required");
            else if (!binding.male.isChecked() && !binding.female.isChecked()) {
                Toast.makeText(this, "Select Gender", Toast.LENGTH_SHORT).show();
            } else {

                String name = binding.name.getText().toString();
                int height = Integer.parseInt(binding.height.getText().toString());
                int weight = Integer.parseInt(binding.weight.getText().toString());
                int age = Integer.parseInt(binding.age.getText().toString());
                int goal = Integer.parseInt(binding.goal.getText().toString());
//                get checked radio button in the group
                View radiobutton = findViewById(binding.radioGroup.getCheckedRadioButtonId());
                int index = binding.radioGroup.indexOfChild(radiobutton);

                RadioButton button = (RadioButton) binding.radioGroup.getChildAt(index);

                String gender = button.getText().toString();

                User user = new User(email, name, height, weight, age, goal, "Moderate", gender);

                Call<User> call = apiService.createUser(user);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(BoardingActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BoardingActivity.this, PreferencesActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(BoardingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

}