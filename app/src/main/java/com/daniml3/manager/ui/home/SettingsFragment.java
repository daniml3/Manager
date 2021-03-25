package com.daniml3.manager.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.extensions.AnimatedButton;

import org.jetbrains.annotations.NotNull;

import static com.daniml3.manager.Constants.TAG;

public class SettingsFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;

    private SharedPreferences sharedPreferences;

    private EditText mBuildTokenBox;

    private DrawerLayout mDrawer;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mContext = context;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        mBuildTokenBox = mActivity.findViewById(R.id.text_box);
        sharedPreferences = mContext.getSharedPreferences("fragment_settings", 0);
        mDrawer = mActivity.findViewById(R.id.drawer_layout);
        AnimatedButton mSaveChangesButton = mActivity.findViewById(R.id.save_changes_button);

        mSaveChangesButton.setOnClickListener(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("build_token", mBuildTokenBox.getText().toString());
            editor.apply();
        });

        mSaveChangesButton.setOnLongClickListener(() -> {
            mDrawer.open();
            Utils.vibrate(mContext);
        });

        mBuildTokenBox.setText(sharedPreferences.getString("build_token", ""));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
