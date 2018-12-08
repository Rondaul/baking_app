package com.ronx.project.bakingapp.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ronx.project.bakingapp.R;
import com.ronx.project.bakingapp.adapters.StepsFragmentPagerAdapter;
import com.ronx.project.bakingapp.models.Recipe;
import com.ronx.project.bakingapp.utils.SnackbarFactory;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeStepDetailActivity extends AppCompatActivity {
    @BindView(R.id.recipe_step_viewpager)
    ViewPager mRecipeStepViewPager;
    @BindView(android.R.id.content)
    View mParentLayout;
    @BindView(R.id.btn_next_step)
    Button nextStepButton;
    @BindView(R.id.btn_previous_step)
    Button previousStepButton;
    @BindView(R.id.tv_step_count)
    TextView mStepCountTextView;

    private Recipe mRecipe;
    private int mStepSelectedPosition;

    public static final String RECIPE_KEY = "recipe_k";
    public static final String STEP_SELECTED_KEY = "step_k";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);

        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(RECIPE_KEY) && bundle.containsKey(STEP_SELECTED_KEY)) {
            mRecipe = bundle.getParcelable(RECIPE_KEY);
            mStepSelectedPosition = bundle.getInt(STEP_SELECTED_KEY);
        } else {
            SnackbarFactory.makeSnackBar(this, mParentLayout, getString(R.string.failed_to_load_recipe), true);
            finish();
        }

        // Show the Up button in the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final StepsFragmentPagerAdapter adapter = new StepsFragmentPagerAdapter(getApplicationContext(), mRecipe.getSteps(), getSupportFragmentManager());
        mRecipeStepViewPager.setAdapter(adapter);
        mRecipeStepViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (actionBar != null) {
                    actionBar.setTitle(mRecipe.getSteps().get(position).getShortDescription());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRecipeStepViewPager.setCurrentItem(mStepSelectedPosition);

        mStepCountTextView.setText(adapter.getPageTitle(mRecipeStepViewPager.getCurrentItem()));

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeStepViewPager.setCurrentItem(mRecipeStepViewPager.getCurrentItem() + 1);
                mStepCountTextView.setText(adapter.getPageTitle(mRecipeStepViewPager.getCurrentItem()));
            }
        });

        previousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeStepViewPager.setCurrentItem(mRecipeStepViewPager.getCurrentItem() - 1);
                mStepCountTextView.setText(adapter.getPageTitle(mRecipeStepViewPager.getCurrentItem()));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
