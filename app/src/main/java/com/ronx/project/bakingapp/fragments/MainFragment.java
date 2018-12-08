package com.ronx.project.bakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.ronx.project.bakingapp.R;
import com.ronx.project.bakingapp.adapters.RecipesAdapter;
import com.ronx.project.bakingapp.api.RecipesApiCallback;
import com.ronx.project.bakingapp.api.RecipesApiManager;
import com.ronx.project.bakingapp.models.Recipe;
import com.ronx.project.bakingapp.utils.GlobalApplication;
import com.ronx.project.bakingapp.utils.Listeners;
import com.ronx.project.bakingapp.utils.Network;
import com.ronx.project.bakingapp.utils.Prefs;
import com.ronx.project.bakingapp.utils.SnackbarFactory;
import com.ronx.project.bakingapp.utils.SpacingItemDecoration;
import com.ronx.project.bakingapp.widget.AppWidgetService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment {

    private static String RECIPES_KEY = "recipes";

    @BindView(R.id.rv_recipe_list)
    RecyclerView mRecipeListRecyclerView;

    private Unbinder mUnbinder;
    private OnRecipeClickListener mOnRecipeClickListener;
    private List<Recipe> mRecipes;
    private GlobalApplication globalApplication;

    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mRecipes == null) {
                loadRecipes();
            }
        }
    };

    public MainFragment() {
        //Empty public constructor is required
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnRecipeClickListener) {
            mOnRecipeClickListener = (OnRecipeClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
            + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnRecipeClickListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        Logger.d("onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        setupRecyclerView();

        // Get the IdlingResource instance
        globalApplication = (GlobalApplication) getActivity().getApplicationContext();

        globalApplication.setIdleState(false);


        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPES_KEY)) {
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPES_KEY);

            mRecipeListRecyclerView.setAdapter(new RecipesAdapter(getActivity().getApplicationContext(), mRecipes, new Listeners.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mOnRecipeClickListener.onRecipeSelected(mRecipes.get(position));
                }
            }));
            updateLayout();
        }

        return view;
    }

    private void setupRecyclerView() {
        mRecipeListRecyclerView.setVisibility(View.GONE);
        mRecipeListRecyclerView.setHasFixedSize(true);
        boolean isTwoPaneMode = getResources().getBoolean(R.bool.isTwoPaneMode);
        if(isTwoPaneMode) {
            mRecipeListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3 ));
        } else {
            mRecipeListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }

        mRecipeListRecyclerView.addItemDecoration(new SpacingItemDecoration((int) getResources().getDimension(R.dimen.margin_medium)));
        mRecipeListRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
    }


    private void loadRecipes() {
        // Set SwipeRefreshLayout that refreshing in case that loadRecipes get called by the networkChangeReceiver
        if (Network.isNetworkAvailable(getActivity().getApplicationContext())) {

            RecipesApiManager.getInstance().getRecipes(new RecipesApiCallback<List<Recipe>>() {
                @Override
                public void onResponse(final List<Recipe> result) {
                    if (result != null) {
                        mRecipes = result;
                        mRecipeListRecyclerView.setAdapter(new RecipesAdapter(getActivity().getApplicationContext(), mRecipes, new Listeners.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                mOnRecipeClickListener.onRecipeSelected(mRecipes.get(position));
                            }
                        }));
                        // Set the default recipe for the widget
                        if (Prefs.loadRecipe(getActivity().getApplicationContext()) == null) {
                            AppWidgetService.updateWidget(getActivity(), mRecipes.get(0));
                        }

                    } else {
                        SnackbarFactory.makeSnackBar(getActivity(), getView(), getString(R.string.failed_to_load_data), true);
                    }

                    updateLayout();
                }

                @Override
                public void onCancel() {
                    updateLayout();
                }

            });
        } else {
            SnackbarFactory.makeSnackBar(getActivity(), getView(), getString(R.string.no_internet), true);
        }
    }

    /**
     * Check if data is loaded and show/hide Recipes RecyclerView & NoDataContainer regarding the recipes data state
     */
    private void updateLayout() {
        boolean loaded = mRecipes != null && mRecipes.size() > 0;

        mRecipeListRecyclerView.setVisibility(loaded ? View.VISIBLE : View.GONE);

        globalApplication.setIdleState(true);

    }


    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipe recipe);
    }
}
