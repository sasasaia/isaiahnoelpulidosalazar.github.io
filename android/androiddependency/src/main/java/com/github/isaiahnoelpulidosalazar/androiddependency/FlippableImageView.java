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

/**
 * A custom {@link AppCompatImageView} that supports animated or instant horizontal flipping
 * between a front image and a back image, similar to a card flip effect.
 *
 * <p>The flip animation is achieved by incrementally adjusting {@link #setRotationY(float)}
 * on the main thread. The flip speed can be configured via the {@link Speed} enum.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * FlippableImageView fiv = new FlippableImageView(context, true, FlippableImageView.Speed.FAST);
 * fiv.setFrontImage(R.drawable.card_front);
 * fiv.setBackImage(R.drawable.card_back);
 * fiv.flip(); // animated flip
 * }</pre>
 */
public class FlippableImageView extends AppCompatImageView {
    Context context;
    int rotation = 0;
    boolean isFlipped = false, reverseBackImage = false;
    Bitmap frontImage, backImage;
    int speed;

    /**
     * Defines the animation speed for the flip effect.
     * Each value represents the number of degrees rotated per animation frame.
     */
    public enum Speed{
        /** Slow flip — 5 degrees per frame. */
        SLOW(5),
        /** Normal flip speed — 15 degrees per frame. */
        NORMAL(15),
        /** Fast flip — 30 degrees per frame. */
        FAST(30);

        final int value;

        Speed(int value){
            this.value = value;
        }
    }

    /**
     * Constructs a {@code FlippableImageView} with default speed ({@link Speed#NORMAL}).
     *
     * @param context the Android {@link Context}
     */
    public FlippableImageView(@NonNull Context context) {
        super(context);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with default speed ({@link Speed#NORMAL}).
     *
     * @param context the Android {@link Context}
     * @param attrs   attribute set from XML layout
     */
    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a style attribute and default speed.
     *
     * @param context      the Android {@link Context}
     * @param attrs        attribute set from XML layout
     * @param defStyleAttr default style attribute
     */
    public FlippableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        speed = Speed.NORMAL.value;
    }

    /**
     * Constructs a {@code FlippableImageView} with a specified animation speed.
     *
     * @param context the Android {@link Context}
     * @param speed   the desired flip animation speed
     */
    public FlippableImageView(@NonNull Context context, Speed speed) {
        super(context);
        this.context = context;
        this.speed = speed.value;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a specified animation speed.
     *
     * @param context the Android {@link Context}
     * @param speed   the desired flip animation speed
     * @param attrs   attribute set from XML layout
     */
    public FlippableImageView(@NonNull Context context, Speed speed, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.speed = speed.value;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a style attribute and specified speed.
     *
     * @param context      the Android {@link Context}
     * @param speed        the desired flip animation speed
     * @param attrs        attribute set from XML layout
     * @param defStyleAttr default style attribute
     */
    public FlippableImageView(@NonNull Context context, Speed speed, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.speed = speed.value;
    }

    /**
     * Constructs a {@code FlippableImageView} with a back-image reversal flag and default speed.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage) {
        super(context);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a back-image reversal flag.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     * @param attrs            attribute set from XML layout
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a style attribute and back-image reversal flag.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     * @param attrs            attribute set from XML layout
     * @param defStyleAttr     default style attribute
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        speed = Speed.NORMAL.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Constructs a {@code FlippableImageView} with both a reversal flag and a specified speed.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     * @param speed            the desired flip animation speed
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed) {
        super(context);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a reversal flag and specified speed.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     * @param speed            the desired flip animation speed
     * @param attrs            attribute set from XML layout
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Constructs a {@code FlippableImageView} from XML with a style attribute,
     * reversal flag, and specified speed.
     *
     * @param context          the Android {@link Context}
     * @param reverseBackImage if {@code true}, the back image is horizontally mirrored
     * @param speed            the desired flip animation speed
     * @param attrs            attribute set from XML layout
     * @param defStyleAttr     default style attribute
     */
    public FlippableImageView(@NonNull Context context, boolean reverseBackImage, Speed speed, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.speed = speed.value;
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Updates the flip animation speed.
     *
     * @param speed the new animation speed to use
     */
    public void setSpeed(Speed speed){
        this.speed = speed.value;
    }

    /**
     * Returns the current front image bitmap.
     *
     * @return the front-side {@link Bitmap}, or {@code null} if not set
     */
    public Bitmap getFrontImage(){
        return frontImage;
    }

    /**
     * Returns the current back image bitmap.
     *
     * <p>If {@code reverseBackImage} is enabled, the returned bitmap will be horizontally mirrored.</p>
     *
     * @return the back-side {@link Bitmap}, or {@code null} if not set
     */
    public Bitmap getBackImage(){
        return backImage;
    }

    /**
     * Sets whether the back image should be horizontally mirrored when assigned.
     *
     * <p>This has no effect on an already-assigned back image; call {@link #setBackImage(int)}
     * or {@link #setBackImage(Drawable)} after changing this flag to apply mirroring.</p>
     *
     * @param reverseBackImage {@code true} to mirror the back image; {@code false} to use it as-is
     */
    public void setReverseBackImage(boolean reverseBackImage){
        this.reverseBackImage = reverseBackImage;
    }

    /**
     * Sets the front image from a drawable resource ID.
     *
     * @param resourceId the drawable resource ID of the front image
     */
    public void setFrontImage(int resourceId){
        frontImage = ((BitmapDrawable) AppCompatResources.getDrawable(context, resourceId)).getBitmap();
    }

    /**
     * Sets the front image from a {@link Drawable}.
     *
     * @param drawable the drawable to use as the front image; must be a {@link BitmapDrawable}
     */
    public void setFrontImage(Drawable drawable){
        frontImage = ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * Sets the back image from a drawable resource ID.
     *
     * <p>If {@code reverseBackImage} is {@code true}, the bitmap is horizontally mirrored
     * before being stored.</p>
     *
     * @param resourceId the drawable resource ID of the back image
     */
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

    /**
     * Sets the back image from a {@link Drawable}.
     *
     * <p>If {@code reverseBackImage} is {@code true}, the bitmap is horizontally mirrored
     * before being stored.</p>
     *
     * @param drawable the drawable to use as the back image; must be a {@link BitmapDrawable}
     */
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

    /**
     * Flips the view instantly to 180° (or back to 0°) without animation.
     *
     * <p>Toggles the Y-rotation between 0 and 180 and updates the flipped state accordingly.</p>
     */
    public void instantFlip(){
        if (!isFlipped){
            isFlipped = true;
            setRotationY(180);
        } else {
            isFlipped = false;
            setRotationY(0);
        }
    }

    /**
     * Starts an animated flip of the view.
     *
     * <p>The view rotates incrementally by {@link #speed} degrees per frame around the Y axis.
     * When the rotation passes 90°, the displayed image switches between the front and back bitmaps.
     * The animation completes at 180° (flipped) or 0° (unflipped) depending on the current state.</p>
     *
     * <p>Requires both {@link #frontImage} and {@link #backImage} to be set before calling.</p>
     */
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

    /**
     * Returns whether the view is currently showing the back image.
     *
     * @return {@code true} if the view is flipped (showing back image); {@code false} if showing front
     */
    public boolean isFlipped(){
        return isFlipped;
    }
}