package com.github.isaiahnoelpulidosalazar.androiddependency;

import static android.view.View.GONE;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import java.util.Objects;

/**
 * A builder-style wrapper around {@link AlertDialog} that displays a rounded, customizable dialog.
 *
 * <p>The dialog is inflated from {@code R.layout.rounded_alert_dialog_layout} and supports
 * a title, an optional scrollable content area, and up to two buttons (left and right)
 * with configurable labels, text colors, and click listeners.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * new RoundedAlertDialog(context)
 *     .setTitle("Confirm")
 *     .setupLeftButton("Cancel", Color.GRAY)
 *     .setupRightButton("OK", Color.RED)
 *     .setupLeftButtonOnClick(v -> dialog.hide())
 *     .setupRightButtonOnClick(v -> performAction())
 *     .show();
 * }</pre>
 */
public class RoundedAlertDialog {
    Context context;
    LayoutInflater layoutInflater;
    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    Button leftButton, rightButton;
    ScrollView scrollView;

    /**
     * Constructs and inflates a new {@code RoundedAlertDialog} for the given context.
     *
     * <p>The scrollable content area is hidden by default until {@link #addView(View)} is called.</p>
     *
     * @param context the Android {@link Context} used to inflate the dialog layout
     */
    public RoundedAlertDialog(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        dialogView = layoutInflater.inflate(R.layout.rounded_alert_dialog_layout, null);
        builder = new AlertDialog.Builder(context)
                .setView(dialogView);
        scrollView = dialogView.findViewById(R.id.rounded_alert_dialog_view);
        scrollView.setVisibility(GONE);
    }

    /**
     * Adds a custom {@link View} to the dialog's scrollable content area and makes it visible.
     *
     * @param view the view to add inside the scroll container
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog addView(View view){
        scrollView.setVisibility(View.VISIBLE);
        scrollView.addView(view);
        return this;
    }

    /**
     * Sets the title text displayed at the top of the dialog.
     *
     * @param title the title string to display
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setTitle(String title){
        final TextView textView = dialogView.findViewById(R.id.rounded_alert_dialog_title);
        textView.setText(title);
        return this;
    }

    /**
     * Sets up the left button with the specified label, making it visible.
     *
     * @param text the label text for the left button
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupLeftButton(String text){
        leftButton = dialogView.findViewById(R.id.rounded_alert_dialog_left_button);
        leftButton.setText(text);
        leftButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets up the right button with the specified label, making it visible.
     *
     * @param text the label text for the right button
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupRightButton(String text){
        rightButton = dialogView.findViewById(R.id.rounded_alert_dialog_right_button);
        rightButton.setText(text);
        rightButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets up the right button with the specified label and text color, making it visible.
     *
     * @param text  the label text for the right button
     * @param color the text color as a packed ARGB integer (e.g., {@link Color#RED})
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupRightButton(String text, int color){
        rightButton = dialogView.findViewById(R.id.rounded_alert_dialog_right_button);
        rightButton.setText(text);
        rightButton.setTextColor(color);
        rightButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets up the left button with the specified label and text color, making it visible.
     *
     * @param text  the label text for the left button
     * @param color the text color as a packed ARGB integer (e.g., {@link Color#GRAY})
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupLeftButton(String text, int color){
        leftButton = dialogView.findViewById(R.id.rounded_alert_dialog_left_button);
        leftButton.setText(text);
        leftButton.setTextColor(color);
        leftButton.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Assigns a click listener to the left button.
     *
     * <p>Must be called after {@link #setupLeftButton(String)} or
     * {@link #setupLeftButton(String, int)}.</p>
     *
     * @param onClickListener the listener to attach to the left button
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupLeftButtonOnClick(View.OnClickListener onClickListener){
        leftButton.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * Assigns a click listener to the right button.
     *
     * <p>Must be called after {@link #setupRightButton(String)} or
     * {@link #setupRightButton(String, int)}.</p>
     *
     * @param onClickListener the listener to attach to the right button
     * @return this {@code RoundedAlertDialog} instance, for chaining
     */
    public RoundedAlertDialog setupRightButtonOnClick(View.OnClickListener onClickListener){
        rightButton.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * Builds and displays the dialog with a transparent window background (required for
     * the rounded corner shape to render correctly).
     */
    public void show(){
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getDecorView().setBackgroundColor(Color.TRANSPARENT);
        alertDialog.show();
    }

    /**
     * Dismisses (cancels) the dialog if it is currently showing.
     */
    public void hide(){
        alertDialog.cancel();
    }
}