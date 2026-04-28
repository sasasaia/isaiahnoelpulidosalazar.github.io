package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Fullscreen {

    public static void enable(AppCompatActivity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        try {
            Objects.requireNonNull(activity.getSupportActionBar()).hide();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void disable(AppCompatActivity activity){
        activity.getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
        try {
            Objects.requireNonNull(activity.getSupportActionBar()).show();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
