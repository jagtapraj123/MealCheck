package com.example.meal_check.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meal_check.R;
import com.example.meal_check.models.Recipe;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter {

    ArrayList<String> ingredientsNames;
    ArrayList<String> ingredientURLs;
    Context context;


    public IngredientsAdapter(ArrayList<String> ingredientsNames, Context context) {
        this.ingredientsNames = ingredientsNames;
        this.context = context;
    }

    public IngredientsAdapter(ArrayList<String> ingredientsNames, ArrayList<String> ingredientURLs, Context context) {
        this.ingredientsNames = ingredientsNames;
        this.ingredientURLs = ingredientURLs;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String ingredientName = ingredientsNames.get(position);
        if (ingredientURLs != null) {
            String ingredientURL = ingredientURLs.get(position);
            ((ViewHolder) holder).bind(ingredientName, ingredientURL);
        } else
            ((ViewHolder) holder).bind(ingredientName);
    }

    @Override
    public int getItemCount() {
        return ingredientsNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(String ingredientName) {
            TextView tvIngredientName = itemView.findViewById(R.id.ingredient_name);
            tvIngredientName.setText(ingredientName);

            ImageView ivIngredientImage = itemView.findViewById(R.id.ingredient_image);
            ivIngredientImage.setImageResource(R.mipmap.ic_launcher);
        }

        public void bind(String ingredientName, String ingredientURL) {
            TextView tvIngredientName = itemView.findViewById(R.id.ingredient_name);
            tvIngredientName.setText(ingredientName);

            ImageView ivIngredientImage = itemView.findViewById(R.id.ingredient_image);
            Glide.with(context).load(ingredientURL).into(ivIngredientImage);
        }

    }


}
