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

public class AnimatedButton extends androidx.appcompat.widget.AppCompatButton
        implements View.OnTouchListener, View.OnLongClickListener {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Context mContext;

    private final String TAG = "AnimatedButton";

    private boolean mBlockOnClick;

    private int mAnimationSpeed;

    private Runnable mOnClickListener;
    private Runnable mOnLongClickListener;

    public AnimatedButton(@NonNull Context context) {
        this(context, null);
    }

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

    public int getAnimationSpeed() {
        return mAnimationSpeed;
    }

    public void setAnimationSpeed(int speed) {
        mAnimationSpeed = speed;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        deprecate("setOnClickListener(OnClickListener listener)",
                "setOnClickListener(Runnable runnable)");
    }

    public void setOnClickListener(Runnable runnable) {
        mOnClickListener = runnable;
    }

    public void clearOnClickListener() {
        mOnClickListener = () -> {
        };
    }

    @Override
    public boolean callOnClick() {
        mOnClickListener.run();
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        return true;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        deprecate("setOnLongClickListener(OnLongClickListener listener)",
                "setOnLongClickListener(Runnable runnable");
    }

    public void setOnLongClickListener(Runnable runnable) {
        mOnLongClickListener = () -> {
            runnable.run();
            mBlockOnClick = true;
        };
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        boolean isActionUp = (event.getAction() == MotionEvent.ACTION_UP);
        boolean restoreSize = (event.getAction() == MotionEvent.ACTION_CANCEL);
        boolean animate = (event.getAction() != MotionEvent.ACTION_MOVE);
        float scale = isActionUp || restoreSize ? 1.0f : 0.95f;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler.postDelayed(mOnLongClickListener, 500);
            mBlockOnClick = false;
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
            if (!mBlockOnClick) {
                callOnClick();
                vibrate();
            }
        }

        return true;
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
