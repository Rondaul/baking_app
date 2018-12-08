package com.ronx.project.bakingapp;

import android.content.Context;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import com.ronx.project.bakingapp.activities.RecipeDetailsActivity;
import com.ronx.project.bakingapp.activities.RecipeStepDetailActivity;
import com.ronx.project.bakingapp.models.Recipe;
import com.ronx.project.bakingapp.utils.BaseTest;
import com.ronx.project.bakingapp.utils.RecyclerViewTest;
import com.ronx.project.bakingapp.utils.Prefs;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BakingRecipesTest extends BaseTest {

    @Test
    public void clickRecyclerViewItemHasIntentWithAKey() {
        //Checks if the key is present
        Intents.init();

        RecyclerViewTest.getMeToRecipeInfo(0);
        intended(hasExtraWithKey(RecipeDetailsActivity.RECIPE_KEY));

        Intents.release();

    }

    @Test
    public void clickOnRecyclerViewItem_opensRecipeInfoActivity() {

        RecyclerViewTest.getMeToRecipeInfo(0);

        onView(withId(R.id.ingredients_text))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recipe_step_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnRecyclerViewStepItem_opensRecipeStepActivity_orFragment() {
        RecyclerViewTest.getMeToRecipeInfo(0);

        boolean twoPaneMode = globalApplication.getResources().getBoolean(R.bool.isTwoPaneMode);
        if (!twoPaneMode) {
            // Checks if the keys are present and the intent launched is RecipeStepDetailActivity
            Intents.init();
            RecyclerViewTest.selectRecipeStep(1);
            intended(hasComponent(RecipeStepDetailActivity.class.getName()));
            intended(hasExtraWithKey(RecipeStepDetailActivity.RECIPE_KEY));
            intended(hasExtraWithKey(RecipeStepDetailActivity.STEP_SELECTED_KEY));
            Intents.release();

            // Check Next and Pervious button
            onView(withId(R.id.btn_next_step))
                    .perform(click());

            onView(withId(R.id.tv_step_count)).check(matches(withText("Step 1")));

            onView(withId(R.id.btn_previous_step))
                    .perform(click());

            onView(withId(R.id.tv_step_count)).check(matches(withText("Step 0")));
        } else {
            RecyclerViewTest.selectRecipeStep(1);

            onView(withId(R.id.exo_player_view))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void checkAddWidgetButtonFunctionality() {
        // Clear the preferences values
        globalApplication.getSharedPreferences(Prefs.PREFS_NAME, Context.MODE_PRIVATE).edit()
                .clear()
                .commit();

        onView(withId(R.id.rv_recipe_list)).perform(RecyclerViewActions.scrollToPosition(3));

        RecyclerViewTest.getMeToRecipeInfo(0);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Add to widget"))
                .check(matches(isDisplayed()))
                .perform(click());

        // Get the recipe base64 string from the sharedPrefs
        Recipe recipe = Prefs.loadRecipe(globalApplication);

        // Assert recipe is not null
        assertNotNull(recipe);
    }

}
