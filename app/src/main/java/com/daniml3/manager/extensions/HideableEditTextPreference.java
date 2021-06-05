package com.daniml3.manager.extensions;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;

public class HideableEditTextPreference extends com.daniml3.manager.components.EditTextPreference
        implements View.OnFocusChangeListener {
    public HideableEditTextPreference(Context context) { this(context, null); }

    public HideableEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnFocusChangeListener(this);
        onFocusChange(this, hasFocus());
    }

    @Override
    public void onFocusChange(View view, boolean focus) {
        int inputType = focus ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                              : InputType.TYPE_TEXT_VARIATION_PASSWORD;
        setInputType(InputType.TYPE_CLASS_TEXT | inputType);
    }
}
