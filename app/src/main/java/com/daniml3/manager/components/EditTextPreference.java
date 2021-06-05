package com.daniml3.manager.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import com.daniml3.manager.R;

public class EditTextPreference extends androidx.appcompat.widget.AppCompatEditText {
    private final SharedPreferences mSharedPreferences;

    private final String mKey;

    private final String TAG = "EditTextPreference";

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        final String mDefaultValue;

        mSharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), 0);

        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.EditTextPreference, 0, 0);

        try {
            mKey = styledAttributes.getString(R.styleable.EditTextPreference_preference);
            mDefaultValue = styledAttributes.getString(R.styleable.EditTextPreference_defaultValue);
        } finally {
            styledAttributes.recycle();
        }

        if (mKey == null || mKey.isEmpty()) {
            Log.e(TAG, "The 'key' attribute is empty");
            throw new RuntimeException();
        }

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSharedPreferences.edit().putString(mKey, s.toString()).apply();
                Log.d(TAG, mKey + " value updated to " + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setText(mSharedPreferences.getString(mKey, mDefaultValue));
    }

    public EditTextPreference(Context context) { this(context, null); }
}
