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

public class RoundedAlertDialog {
    Context context;
    LayoutInflater layoutInflater;
    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    Button leftButton, rightButton;
    ScrollView scrollView;

    public RoundedAlertDialog(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        dialogView = layoutInflater.inflate(R.layout.rounded_alert_dialog_layout, null);
        builder = new AlertDialog.Builder(context)
                .setView(dialogView);
        scrollView = dialogView.findViewById(R.id.rounded_alert_dialog_view);
        scrollView.setVisibility(GONE);
    }

    public RoundedAlertDialog addView(View view){
        scrollView.setVisibility(View.VISIBLE);
        scrollView.addView(view);
        return this;
    }

    public RoundedAlertDialog setTitle(String title){
        final TextView textView = dialogView.findViewById(R.id.rounded_alert_dialog_title);
        textView.setText(title);
        return this;
    }

    public RoundedAlertDialog setupLeftButton(String text){
        leftButton = dialogView.findViewById(R.id.rounded_alert_dialog_left_button);
        leftButton.setText(text);
        leftButton.setVisibility(View.VISIBLE);
        return this;
    }

    public RoundedAlertDialog setupRightButton(String text){
        rightButton = dialogView.findViewById(R.id.rounded_alert_dialog_right_button);
        rightButton.setText(text);
        rightButton.setVisibility(View.VISIBLE);
        return this;
    }

    public RoundedAlertDialog setupRightButton(String text, int color){
        rightButton = dialogView.findViewById(R.id.rounded_alert_dialog_right_button);
        rightButton.setText(text);
        rightButton.setTextColor(color);
        rightButton.setVisibility(View.VISIBLE);
        return this;
    }

    public RoundedAlertDialog setupLeftButton(String text, int color){
        leftButton = dialogView.findViewById(R.id.rounded_alert_dialog_left_button);
        leftButton.setText(text);
        leftButton.setTextColor(color);
        leftButton.setVisibility(View.VISIBLE);
        return this;
    }

    public RoundedAlertDialog setupLeftButtonOnClick(View.OnClickListener onClickListener){
        leftButton.setOnClickListener(onClickListener);
        return this;
    }

    public RoundedAlertDialog setupRightButtonOnClick(View.OnClickListener onClickListener){
        rightButton.setOnClickListener(onClickListener);
        return this;
    }

    public void show(){
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getDecorView().setBackgroundColor(Color.TRANSPARENT);
        alertDialog.show();
    }

    public void hide(){
        alertDialog.cancel();
    }
}
