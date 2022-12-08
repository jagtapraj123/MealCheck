package com.example.meal_check.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meal_check.R;
import com.example.meal_check.models.Recipe;

import java.util.ArrayList;

//search adapter for recycler view in search activity to display search results of recipes' names
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Recipe> recipes;
    private Context context;
    private SearchAdapter.OnItemClickListener listener;

    private ArrayList<Integer> ids;


    public SearchAdapter(ArrayList<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
        ids = new ArrayList<>();
    }


    public SearchAdapter(Context context, ArrayList<Recipe> recipes, OnItemClickListener onItemClickListener) {
        this.recipes = recipes;
        this.context = context;
        this.listener = onItemClickListener;
        ids = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        return new ViewHolder(view);
    }

    public void updateRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public void updateIds(ArrayList<Integer> ids) {
        this.ids = ids;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SearchAdapter.OnItemClickListener listener) {
        this.listener = listener;
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRecipeName;
        private ImageView tick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tick = itemView.findViewById(R.id.tick);

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
            tvRecipeName.setText(recipe.getName());
            if (ids.contains(recipe.getId())) {
                tick.setVisibility(View.VISIBLE);
            } else {
                tick.setVisibility(View.INVISIBLE);
            }

        }
    }
}
