package com.ronx.project.bakingapp.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class SnackbarFactory {
    /**
     * Create SnackbarFactory to show a message and set its background color regarding message type (Error - Info)
     */
    public static void makeSnackBar(Context context, View view, String text, boolean error) {
        final Snackbar snackbar;
        snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
//       snackBarView.setBackgroundColor(ContextCompat.getColor(context, error ? R.color.colorError : R.color.colorInfo));
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text); //Get reference of snackbar textview
        textView.setMaxLines(3);

        snackbar.show();
    }
}
