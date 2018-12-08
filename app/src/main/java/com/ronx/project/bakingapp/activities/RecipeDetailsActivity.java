package com.ronx.project.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.ronx.project.bakingapp.R;
import com.ronx.project.bakingapp.adapters.RecipeAdapter;
import com.ronx.project.bakingapp.fragments.RecipeStepDetailFragment;
import com.ronx.project.bakingapp.models.Recipe;
import com.ronx.project.bakingapp.utils.Listeners;
import com.ronx.project.bakingapp.utils.SnackbarFactory;
import com.ronx.project.bakingapp.utils.SpacingItemDecoration;
import com.ronx.project.bakingapp.widget.AppWidgetService;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity {
    public static final String RECIPE_KEY = "recipe_key";

    @BindView(R.id.recipe_step_list)
    RecyclerView mRecyclerView;

    @BindView(android.R.id.content)
    View mParentLayout;

    private boolean mTwoPane;

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_KEY)) {
            mRecipe = bundle.getParcelable(RECIPE_KEY);
        } else {
            SnackbarFactory.makeSnackBar(this, mParentLayout, getString(R.string.failed_to_load_recipe), true);
            finish();
        }

        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar and set recipes name as title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTwoPane = getResources().getBoolean(R.bool.isTwoPaneMode);
        if (mTwoPane) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.

            // If there is no fragment state and the recipe contains steps, show the 1st one
            if (savedInstanceState == null && !mRecipe.getSteps().isEmpty()) {
                showStep(0);
            }
        }

        setupRecyclerView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("onDestroy");
    }

    private void setupRecyclerView() {
        mRecyclerView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.margin_medium)));
        mRecyclerView.setAdapter(new RecipeAdapter(mRecipe, new Listeners.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showStep(position);
            }
        }));
    }

    private void showStep(int position) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeStepDetailFragment.STEP_KEY, mRecipe.getSteps().get(position));
            RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeStepDetailActivity.class);
            intent.putExtra(RecipeStepDetailActivity.RECIPE_KEY, mRecipe);
            intent.putExtra(RecipeStepDetailActivity.STEP_SELECTED_KEY, position);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_to_widget) {
            AppWidgetService.updateWidget(this, mRecipe);
            SnackbarFactory.makeSnackBar(this, mParentLayout, String.format(getString(R.string.added_to_widget), mRecipe.getName()), false);

            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
