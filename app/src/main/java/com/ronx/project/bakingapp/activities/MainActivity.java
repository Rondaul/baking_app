package com.ronx.project.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ronx.project.bakingapp.R;
import com.ronx.project.bakingapp.fragments.MainFragment;
import com.ronx.project.bakingapp.models.Recipe;

public class MainActivity extends AppCompatActivity implements MainFragment.OnRecipeClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(RecipeDetailsActivity.RECIPE_KEY, recipe);
        startActivity(intent);
    }
}
