package com.ronx.project.bakingapp.api;

public interface RecipesApiCallback<T> {
    void onResponse(T result);

    void onCancel();
}
