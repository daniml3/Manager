package com.daniml3.manager.components;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

public class ViewAnimator {
    private final View mTargetView;

    private final Handler mHandler;

    private int mLoadingAnimationDuration;

    private boolean mLoadingAnimating;

    public ViewAnimator(View targetView) {
        mTargetView = targetView;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void startLoadingAnimation(int duration) {
        if (isLoadingAnimating()) {
            return;
        }

        mLoadingAnimationDuration = duration;
        mLoadingAnimating = true;
        scheduleFadeRunnable(false, mLoadingAnimationDuration).run();
    }

    public void stopLoadingAnimation() {
        mLoadingAnimating = false;
        mTargetView.animate().alpha(1.0f).setDuration(mLoadingAnimationDuration / 4).start();
    }

    private Runnable scheduleFadeRunnable(boolean fadeOut, int delay) {
        return () -> mHandler.post(() -> {
            if (isLoadingAnimating()) {
                mTargetView.animate()
                        .alpha(fadeOut ? 1.0f : 0.5f)
                        .setDuration(mLoadingAnimationDuration)
                        .start();
                mHandler.postDelayed(scheduleFadeRunnable(!fadeOut, delay), delay);
            }
        });
    }

    public boolean isLoadingAnimating() { return mLoadingAnimating; }

    public void startSmoothLayoutChange(int duration) {
        TransitionManager.beginDelayedTransition(
                (ViewGroup) mTargetView.getRootView(), new AutoTransition().setDuration(duration));
    }
}
