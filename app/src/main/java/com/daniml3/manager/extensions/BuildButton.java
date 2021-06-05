package com.daniml3.manager.extensions;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

public class BuildButton extends MultipleStateButton {
    private static final String TAG = "BuildButton";

    private String[] mValueList;

    public BuildButton(@NonNull Context context) { super(context); }

    public String getCurrentBuildVariableValue() {
        if (mValueList.length == 0 || mValueList.length <= getState()) {
            Log.e(TAG, "Invalid value list");
            throw new RuntimeException();
        }

        return mValueList[getState()];
    }

    public void setVariableValueListForValues(String[] valueListForValues) {
        if (valueListForValues.length - 1 > getMaxStates()) {
            setMaximumState(valueListForValues.length - 1);
        }

        mValueList = valueListForValues;
    }
}
