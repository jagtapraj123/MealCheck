package com.example.meal_check.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private Context context;
    private OnItemClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    public RecipeAdapter(List<Recipe> recipes, Context context, OnItemClickListener listener) {
        this.recipes = recipes;
        this.context = context;
        this.listener = listener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvDescription;
        private ImageView ivImage;
        private TextView tvPrepTime;
        private TextView tvNutrition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.recipe_name);
            tvDescription = itemView.findViewById(R.id.recipe_description);
            ivImage = itemView.findViewById(R.id.recipe_image);
            tvPrepTime = itemView.findViewById(R.id.prep_time);


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            tvName.setText(recipe.getName());
            tvDescription.setText(recipe.getDescription());
            String prepTime = recipe.getPrepTime() + " minutes";
            tvPrepTime.setText(prepTime);
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                Glide.with(context).load(recipe.getImage()).into(ivImage);
            }


        }
    }
}
