package com.ronx.project.bakingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ronx.project.bakingapp.R;
import com.ronx.project.bakingapp.models.Recipe;
import com.ronx.project.bakingapp.utils.GlideApp;
import com.ronx.project.bakingapp.utils.Listeners;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private Context mContext;
    private List<Recipe> mRecipes;
    private Listeners.OnItemClickListener mOnItemClickListener;

    public RecipesAdapter(Context context, List<Recipe> recipes, Listeners.OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mRecipes = recipes;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);

        return new RecipeViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, final int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }


    class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_name_text)
        TextView mRecipeNameTextView;

        @BindView(R.id.servings_text)
        TextView mServingsTextView;

        @BindView(R.id.recipe_image)
        AppCompatImageView mRecipeImageView;

        RecipeViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            mRecipeNameTextView.setText(mRecipes.get(position).getName());
            mServingsTextView.setText(mContext.getString(R.string.servings, mRecipes.get(position).getServings()));

            String recipeImage = mRecipes.get(position).getImage();
            if (!recipeImage.isEmpty()) {
                GlideApp.with(mContext)
                        .load(recipeImage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.cake_image_300)
                        .into(mRecipeImageView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
