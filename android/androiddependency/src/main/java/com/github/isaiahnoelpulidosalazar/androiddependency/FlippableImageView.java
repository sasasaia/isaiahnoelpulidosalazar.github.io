package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

public class FlippableImageView extends AppCompatImageView {
    Context context;
    int rotation = 0;
    boolean isFlipped = false, reverseBackImage = false;
    Bitmap frontImage, backImage;
    int speed;

    public enum Speed{
        SLOW(5),
        NORMAL(15),
        FAST(30);

        final int value;

        Speed(int value){
            this.value = value;
        }
    }

    public FlippableImageView(@NonNull Context context) {
        super(context);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    public FlippableImageView(@NonNull Context context, Speed speed) {
        super(context);
        this.context = context;
        this.speed = speed.value;
    }

    public FlippableImageView(@NonNull Context context, Speed speed, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.speed = speed.value;
    }

    public FlippableImageView(@NonNull Context context, Speed speed, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.speed = speed.value;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage) {
        super(context);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed) {
        super(context);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    public void setSpeed(Speed speed){
        this.speed = speed.value;
    }

    public Bitmap getFrontImage(){
        return frontImage;
    }

    public Bitmap getBackImage(){
        return backImage;
    }

    public void setReverseBackImage(boolean reverseBackImage){
        this.reverseBackImage = reverseBackImage;
    }

    public void setFrontImage(int resourceId){
        frontImage = ((BitmapDrawable) AppCompatResources.getDrawable(context, resourceId)).getBitmap();
    }

    public void setFrontImage(Drawable drawable){
        frontImage = ((BitmapDrawable) drawable).getBitmap();
    }

    public void setBackImage(int resourceId){
        if (reverseBackImage){
            Bitmap temp = ((BitmapDrawable) AppCompatResources.getDrawable(context, resourceId)).getBitmap();
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1, temp.getWidth() / 2, temp.getHeight() / 2);
            backImage = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
        } else {
            backImage = ((BitmapDrawable) AppCompatResources.getDrawable(context, resourceId)).getBitmap();
        }
    }

    public void setBackImage(Drawable drawable){
        if (reverseBackImage){
            Bitmap temp = ((BitmapDrawable) drawable).getBitmap();
            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1, temp.getWidth() / 2, temp.getHeight() / 2);
            backImage = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
        } else {
            backImage = ((BitmapDrawable) drawable).getBitmap();
        }
    }

    public void instantFlip(){
        if (!isFlipped){
            isFlipped = true;
            setRotationY(180);
        } else {
            isFlipped = false;
            setRotationY(0);
        }
    }

    public void flip(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!isFlipped){
                    rotation += speed;
                    if (rotation >= 90 && rotation < 180){
                        setRotationY(rotation);
                        new Handler().post(this);
                        setImageBitmap(backImage);
                    } else if (rotation < 180){
                        setRotationY(rotation);
                        new Handler().post(this);
                    }
                    if (rotation == 180){
                        isFlipped = true;
                        setRotationY(rotation);
                    }
                } else {
                    rotation -= speed;
                    if (rotation >= 90 && rotation < 180){
                        setRotationY(rotation);
                        new Handler().post(this);
                    } else if (rotation > 0 && rotation < 90){
                        setRotationY(rotation);
                        new Handler().post(this);
                        setImageBitmap(frontImage);
                    }
                    if (rotation == 0){
                        isFlipped = false;
                        setRotationY(rotation);
                    }
                }
            }
        });
    }

    public boolean isFlipped(){
        return isFlipped;
    }
}
