package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * A utility class for enabling or disabling fullscreen (immersive) mode in an
 * {@link AppCompatActivity}.
 *
 * <p>Fullscreen mode hides the status bar, navigation bar, and action bar.
 * It uses the sticky immersive flag so that the UI remains hidden even after the
 * user accidentally swipes from an edge.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // In your Activity's onResume or onWindowFocusChanged:
 * Fullscreen.enable(this);
 *
 * // To restore normal UI:
 * Fullscreen.disable(this);
 * }</pre>
 */
public class Fullscreen {

    /**
     * Enables fullscreen (immersive sticky) mode for the given activity.
     *
     * <p>This sets the system UI visibility flags to hide the status bar, navigation bar,
     * and applies sticky immersive mode. The activity's action bar is also hidden if present.</p>
     *
     * <p>Note: {@link View#SYSTEM_UI_FLAG_FULLSCREEN} and related flags are deprecated in
     * API level 30. For apps targeting API 30+, consider using
     * {@link android.view.WindowInsetsController} instead.</p>
     *
     * @param activity the {@link AppCompatActivity} to put into fullscreen mode
     */
    public static void enable(AppCompatActivity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        try {
            Objects.requireNonNull(activity.getSupportActionBar()).hide();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
     * Disables fullscreen mode and restores the normal system UI for the given activity.
     *
     * <p>Resets the system UI visibility to {@link View#VISIBLE} and re-shows the action bar
     * if one is present.</p>
     *
     * @param activity the {@link AppCompatActivity} to take out of fullscreen mode
     */
    public static void disable(AppCompatActivity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
        try {
            Objects.requireNonNull(activity.getSupportActionBar()).show();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}