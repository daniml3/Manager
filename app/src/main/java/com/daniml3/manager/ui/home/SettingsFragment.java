package com.daniml3.manager.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daniml3.manager.Constants;
import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.extensions.AnimatedButton;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends Fragment {

    private Activity mActivity;
    private Context mContext;

    private SharedPreferences sharedPreferences;

    private EditText mBuildTokenBox;

    private SeekBar mBuildCardCountBar;

    private TextView mBuildInfoCardCountText;

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
        mBuildCardCountBar = mActivity.findViewById(R.id.build_card_count);
        sharedPreferences = mContext.getSharedPreferences(Constants.SETTINGS_PREFERENCES, 0);
        mDrawer = mActivity.findViewById(R.id.drawer_layout);
        AnimatedButton mSaveChangesButton = mActivity.findViewById(R.id.save_changes_button);
        mBuildInfoCardCountText = mActivity.findViewById(R.id.build_card_count_text);

        mSaveChangesButton.setOnClickListener(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.BUILD_TOKEN_PREFERENCE, mBuildTokenBox.getText().toString());
            editor.putInt(Constants.BUILD_CAR_COUNT_PREFERENCE, (mBuildCardCountBar.getProgress()));
            editor.apply();
        });

        mSaveChangesButton.setOnLongClickListener(() -> {
            mDrawer.open();
            Utils.vibrate(mContext);
        });

        mBuildTokenBox.setText(sharedPreferences.getString(Constants.BUILD_TOKEN_PREFERENCE, ""));
        mBuildCardCountBar.setProgress(sharedPreferences.getInt(Constants.BUILD_CAR_COUNT_PREFERENCE, Constants.BUILD_CARD_COUNT_DEFAULT));
        mBuildInfoCardCountText.setText(String.valueOf(mBuildCardCountBar.getProgress()));

        mBuildCardCountBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBuildInfoCardCountText.setText(String.valueOf(mBuildCardCountBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}