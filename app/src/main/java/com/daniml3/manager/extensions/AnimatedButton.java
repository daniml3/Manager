package com.daniml3.manager.extensions;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.daniml3.manager.Utils;

public class AnimatedButton  extends androidx.appcompat.widget.AppCompatButton
        implements View.OnTouchListener, View.OnLongClickListener {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Context mContext;
    private final String TAG = "AnimatedButton";

    private boolean mBlockOnClick;

    private int mAnimationSpeed;

    private Runnable mOnClickListener;
    private Runnable mOnLongClickListener;

    private boolean mAnimating = false;

    private final int FADE_ANIMATION_DURATION = 1000;

    /*
    * Main constructor with only Context
    */
    public AnimatedButton(@NonNull Context context) {
        this(context, null);
    }

    /*
    * Main constructor with Context and AttributeSet
    */
    public AnimatedButton(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;

        setOnTouchListener(this);
        ShapeDrawable shape = new ShapeDrawable(
                new RoundRectShape(
                        new float[]{30, 30, 30, 30, 30, 30, 30, 30}, null, null));

        setBackgroundDrawable(shape);
        setOnClickListener(() -> Utils.vibrate(mContext));
        setOnTouchListener(this);
        setAnimationSpeed(100);
    }

    public void setAnimationSpeed(int speed) {
        mAnimationSpeed = speed;
    }

    public int getAnimationSpeed() {
        return mAnimationSpeed;
    }

    /*
    * Deprecate setOnClickListener(OnClickListener listener)
    */
    @Override
    public void setOnClickListener(OnClickListener listener) {
        deprecate("setOnClickListener(OnClickListener listener)",
                "setOnClickListener(Runnable runnable)");
    }

    /*
    * Sets a runnable to be executed after a click
    */
    public void setOnClickListener(Runnable runnable) {
        mOnClickListener = runnable;
    }

    /*
    * Removes the onClick callback
    */
    public void clearOnClickListener() {
        mOnClickListener = () -> {};
    }

    /*
    * Execute the onClick callback
    */
    @Override
    public boolean callOnClick() {
        mOnClickListener.run();
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        return true;
    }

    /*
    * Deprecate setOnLongClickListener(OnLongClickListener listener)
    */
    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        deprecate("setOnLongClickListener(OnLongClickListener listener)",
                "setOnLongClickListener(Runnable runnable");
    }

    /*
    * Sets a runnable to be executed after a long click
    */
    public void setOnLongClickListener(Runnable runnable) {
        mOnLongClickListener = () -> {
            runnable.run();
            mBlockOnClick = true;
        };
    }

    /*
     * On touch listener
     * This will handle the cases when the user touches the button
     *
     * It will make an animation that changes the button size at touch
     *
     * It will also handle the cases where the button is being swiped on, but a consistent click,
     * which will make the button unresponsive, but will restore it to the original size
     * This is done by comparing the original coordinates and seeing if they have changed more
     * than the width / height of the view
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean isActionUp = (event.getAction() == MotionEvent.ACTION_UP);
        boolean restoreSize = (event.getAction() == MotionEvent.ACTION_CANCEL);
        boolean animate = (event.getAction() != MotionEvent.ACTION_MOVE);
        float scale = isActionUp || restoreSize ? 1.0f : 0.95f;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler.postDelayed(mOnLongClickListener, 500);
        }

        if (animate) {
            AnimatorSet sizeAnimation = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scale);

            scaleX.setDuration(getAnimationSpeed());
            scaleY.setDuration(getAnimationSpeed());

            sizeAnimation.play(scaleX).with(scaleY);
            sizeAnimation.start();
        }

        if (isActionUp) {
            mHandler.removeCallbacks(mOnLongClickListener);
            if (!mBlockOnClick && !mAnimating) {
                callOnClick();
                vibrate();
            }
        }
        return true;
    }



    public void startLoadingAnimation() {
        if (mAnimating) {
            return;
        }
        mAnimating = true;
        new Thread(() -> scheduleFadeRunnable(false, FADE_ANIMATION_DURATION).run()).start();
    }

    private Runnable scheduleFadeRunnable(boolean fadeOut, int delay) {
        return () -> mHandler.post(() -> {
            if (mAnimating) {
                animate().alpha(fadeOut ? 1.0f : 0.5f).setDuration(FADE_ANIMATION_DURATION).start();
                mHandler.postDelayed(scheduleFadeRunnable(!fadeOut, delay), delay);
            } else {
                animate().alpha(1.0f).setDuration(FADE_ANIMATION_DURATION).start();
            }
        });
    }

    public void stopLoadingAnimation() {
        mHandler.postDelayed(() -> mAnimating = false, FADE_ANIMATION_DURATION);
    }

    public boolean isAnimating() {
        return mAnimating;
    }

    /*
     * Returns true if the difference between coordinates is higher than
     * the maximum difference set on the third argument
     */
    private boolean compareCoordinates(float originalCoordinate, float currentCoordinate, float maximumDifference) {
        float biggerCoordinate = Math.max(originalCoordinate, currentCoordinate);
        float smallerCoordinate = Math.min(originalCoordinate, currentCoordinate);
        return biggerCoordinate - smallerCoordinate > maximumDifference;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    private void deprecate(String deprecation, String alternative) {
        Log.e(TAG, String.format("%s is deprecated, please use %s instead", deprecation, alternative));
        throw new RuntimeException();
    }
}
