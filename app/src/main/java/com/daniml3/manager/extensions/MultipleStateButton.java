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

    public MultipleStateButton(@NonNull Context context) {
        super(context);
        mContext = context;

        setOnClickListener(() -> {
            incrementState();
            updateState();
        });
    }

    /*
    * Set the maximum state on the button, starting from 0
    */
    public void setMaximumState(int state) {
        mMaxState = state;
    }

    /*
    * @return the current state
    */
    public int getState() {
        return mState;
    }

    /*
    * Set the given state
    */
    public void setState(int state) {
        mState = state;
        updateState();
    }

    /*
    * Set the current maximum state
    */
    public int getMaxStates() {
        return mMaxState;
    }

    /*
    * Set the color list, which contains the colors that will be used for each state
    */
    public void setColorListForStates(int[] colorList) {
        mColorList = colorList;
        updateBackgroundTint();
    }

    /*
    * Set the text list, which contains the strings that will be used for each state
    */
    public void setTextListForStates(int[] stringList) {
        mTextList = stringList;
        updateText();
    }

    private void incrementState() {
        if (mState < mMaxState) {
            mState = mState + 1;
        } else {
            mState = 0;
        }
    }

    /*
    * Update the current state, and increment it if told to
    */
    private void updateState() {
        updateBackgroundTint();
        updateText();
    }

    /*
    * Update the current background depending on the state
    */
    private void updateBackgroundTint() {
        if (mColorList.length == 0) {
            return;
        }

        setBackgroundTintList(mContext.getColorStateList(mColorList[mState]));
    }

    /*
    * Update the text to match the current state
    */
    private void updateText() {
        if (mTextList.length == 0) {
            return;
        }

        setText(mTextList[mState]);
    }

    /*
    * @return true if the state is invalid
    */
    private boolean isStateInvalid(int state) {
        return (state > mMaxState || state < 0);
    }

    /*
    * Validate the current switch configuration
    * @throw a runtime exception if an invalid configuration is found
    */
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
