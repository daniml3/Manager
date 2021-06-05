package com.daniml3.manager.extensions;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

public class MultipleStateButton extends AnimatedButton {
    private static final String TAG = "MultipleStateButton";

    private final Context mContext;

    private int mState = 0;
    private int mMaxState = 1;

    private int[] mColorList = new int[] {};
    private int[] mTextList = new int[] {};

    private boolean mRetainHeight;
    private boolean mRetainWidth;

    public MultipleStateButton(@NonNull Context context) {
        super(context);
        mContext = context;

        setOnClickListener(() -> {
            int initialHeight = getHeight();
            int initialWidth = getWidth();

            incrementState();
            update();

            if (mRetainHeight) {
                setHeight(initialHeight);
            }

            if (mRetainWidth) {
                setWidth(initialWidth);
            }
        });
    }

    public void setMaximumState(int state) { mMaxState = state; }

    public int getState() { return mState; }

    public void setState(int state) {
        mState = state;
        update();
    }

    public int getMaxStates() { return mMaxState; }

    public void setColorListForStates(int[] colorList) {
        mColorList = colorList;
        updateBackgroundTint();
    }

    public void setTextListForStates(int[] stringList) {
        mTextList = stringList;
        updateText();
    }

    public void setRetainHeight(boolean retain) { mRetainHeight = retain; }

    public void setRetainWidth(boolean retain) { mRetainWidth = retain; }

    private void incrementState() {
        if (mState < mMaxState) {
            mState = mState + 1;
        } else {
            mState = 0;
        }
    }

    private void update() {
        updateBackgroundTint();
        updateText();
    }

    private void updateBackgroundTint() {
        if (mColorList.length == 0) {
            return;
        }

        setBackgroundTintList(mContext.getColorStateList(mColorList[mState]));
    }

    private void updateText() {
        if (mTextList.length == 0) {
            return;
        }

        setText(mTextList[mState]);
    }

    private boolean isStateInvalid(int state) { return (state > mMaxState || state < 0); }

    public void validate() {
        if (isStateInvalid(mState)) {
            Log.e(TAG, "Invalid state " + mState);
        } else if (mColorList.length <= mMaxState) {
            Log.e(TAG, "Color list is too short for the maximum value");
        } else if (mTextList.length <= mMaxState) {
            Log.e(TAG, "Text list is too short for the maximum value");
        } else {
            return;
        }

        throw new RuntimeException();
    }
}
