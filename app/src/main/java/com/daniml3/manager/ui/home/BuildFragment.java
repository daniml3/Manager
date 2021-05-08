package com.daniml3.manager.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.daniml3.manager.Constants;
import com.daniml3.manager.NetUtils;
import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.components.CardService;
import com.daniml3.manager.components.ViewAnimator;
import com.daniml3.manager.extensions.AnimatedButton;
import com.daniml3.manager.extensions.BuildButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.daniml3.manager.Constants.TAG;

public class BuildFragment extends Fragment {

    private final int BUTTON_FADE_DURATION = 1000;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Activity mActivity;

    private Context mContext;

    private AnimatedButton mTriggerBuildButton;

    private String mBuildToken;

    private SharedPreferences sharedPreferences;

    private ArrayList<String> mArgumentList;
    private ArrayList<BuildButton> mBuildButtonList;
    private ArrayList<LinearLayout> mLinearLayoutList;

    private ScrollView mMainContainer;

    private int mBuildInfoCardCount;

    private boolean mPreparingUI;

    private ViewAnimator mTriggerBuildButtonAnimator;

    private CardService mCardService;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mContext = context;
    }

    @Override
    public void onViewCreated(@NotNull View createdView, Bundle savedInstanceState) {
        mBuildButtonList = new ArrayList<>();
        mArgumentList = new ArrayList<>();
        mLinearLayoutList = new ArrayList<>();

        sharedPreferences = mActivity.getSharedPreferences(Constants.SETTINGS_PREFERENCES, 0);
        mTriggerBuildButton = mActivity.findViewById(R.id.build_button);
        mMainContainer = mActivity.findViewById(R.id.main_build_container);

        mBuildToken = sharedPreferences.getString(Constants.BUILD_TOKEN_PREFERENCE, "");
        mBuildInfoCardCount = sharedPreferences.getInt(Constants.BUILD_CAR_COUNT_PREFERENCE, Constants.BUILD_CARD_COUNT_DEFAULT);

        mTriggerBuildButtonAnimator = new ViewAnimator(mTriggerBuildButton);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        mBuildButtonList.clear();

        CardView configCardView = mActivity.findViewById(R.id.build_config_card);
        configCardView.setOnLongClickListener(v -> {
            Utils.vibrate(mContext);
            return true;
        });

        generateConfigButtons(layoutParams);
        prepareUI();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_build, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mCardService != null) {
            mCardService.deInit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCardService != null) {
            mCardService.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCardService != null) {
            mCardService.resume();
        }
    }

    private LinearLayout getLinearLayout(int currentItemCount) {
        if (currentItemCount % 3 == 0) {
            LinearLayout linearLayout = new LinearLayout(mContext);

            linearLayout.setPadding(0, 10, 0, 10);
            linearLayout.setGravity(Gravity.CENTER);

            ((LinearLayout) mActivity.findViewById(R.id.switch_container)).addView(linearLayout);

            mLinearLayoutList.add(linearLayout);
            return linearLayout;
        }

        return mLinearLayoutList.get(mLinearLayoutList.size() - 1);
    }

    private void showSnackBar(String content) {
        Snackbar.make(mActivity.findViewById(R.id.build_snack_bar_placeholder), content, Snackbar.LENGTH_SHORT).show();
    }

    private Runnable getBuildScheduleRunnable() {
        return () -> {
            mArgumentList.clear();

            for (BuildButton buildButton : mBuildButtonList) {
                mArgumentList.add(buildButton.getCurrentBuildVariableValue());
            }

            Log.d(TAG, mArgumentList.toString());

            if (Utils.getServerAvailableResponse().length() == 0) {
                Log.d(TAG, "Server unavailable");

                mActivity.runOnUiThread(() -> showSnackBar(mActivity.getString(R.string.server_unavailable)));
                return;
            }

            mActivity.runOnUiThread(() -> mTriggerBuildButtonAnimator.stopLoadingAnimation());
            NetUtils.getJSONResponse(Utils.getSchedulingUrl(
                    sharedPreferences.getString(Constants.BUILD_TOKEN_PREFERENCE, ""), mArgumentList));
            mActivity.runOnUiThread(() -> mTriggerBuildButtonAnimator.stopLoadingAnimation());
        };
    }

    private void prepareUI() {
        if (mPreparingUI) {
            return;
        }

        mBuildToken = sharedPreferences.getString(Constants.BUILD_TOKEN_PREFERENCE, "");
        mPreparingUI = true;
        mMainContainer.fullScroll(ScrollView.FOCUS_UP);

        new Thread(() -> {
            LinearLayout cardContainer = mActivity.findViewById(R.id.build_info_card_container);

            mActivity.runOnUiThread(() -> {
                if (mCardService != null) {
                    mCardService.deInit();
                }

                if (mBuildToken.isEmpty()) {
                    mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.button_disabled));
                    mTriggerBuildButton.setText(R.string.missing_build_token);
                    mTriggerBuildButton.clearOnClickListener();
                } else {
                    mTriggerBuildButtonAnimator.startLoadingAnimation(BUTTON_FADE_DURATION);
                }
            });

            boolean isServerAvailable = Utils.isServerAvailable();
            if (!mBuildToken.isEmpty()) {
                mActivity.runOnUiThread(() -> mTriggerBuildButton.setText(R.string.getting_status));

                mActivity.runOnUiThread(() -> {
                    if (!isServerAvailable) {
                        mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.button_disabled));
                        mTriggerBuildButton.setText(R.string.server_unavailable);
                        showSnackBar(mActivity.getString(R.string.server_unavailable));
                        mTriggerBuildButton.setOnClickListener(this::prepareUI);
                    } else if (!mBuildToken.isEmpty()) {
                        mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.blue));
                        mTriggerBuildButton.setText(R.string.menu_build);
                        mTriggerBuildButton.setOnClickListener(() -> getBuildConfirmationDialog().show());
                    }
                });
            }

            mHandler.post(() -> {
                if (isServerAvailable) {
                    showSnackBar(mActivity.getString(R.string.fetching_build_info));
                }
                mCardService = new CardService(cardContainer, mBuildInfoCardCount, mContext);
                mCardService.init();
            });

            mActivity.runOnUiThread(() -> mTriggerBuildButtonAnimator.stopLoadingAnimation());
            mPreparingUI = false;
        }).start();
    }

    private void generateConfigButtons(LinearLayout.LayoutParams layoutParams) {
        int i = 0;
        mBuildButtonList.clear();

        try {
            for (int[] switchTextList : Constants.TEXTS_FOR_SWITCHES) {
                BuildButton buildButton = new BuildButton(mActivity);
                LinearLayout linearLayout = getLinearLayout(i);

                buildButton.setMaximumState(Constants.SWITCHES_MAX_VALUES[i]);
                buildButton.setColorListForStates(Constants.COLORS_FOR_SWITCHES[i]);
                buildButton.setVariableValueListForValues(Constants.VALUES_FOR_SWITCHES[i]);
                buildButton.setTextListForStates(switchTextList);

                buildButton.setState(Constants.DEFAULT_SWITCHES_VALUES[i]);
                buildButton.setLayoutParams(layoutParams);
                buildButton.validate();

                buildButton.setRetainHeight(true);
                buildButton.setRetainWidth(true);

                mBuildButtonList.add(buildButton);
                linearLayout.addView(buildButton);
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Invalid configuration for the buttons");
            throw new RuntimeException();
        }
    }

    private AlertDialog.Builder getBuildConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setPositiveButton(R.string.accept, (dialog, which) -> new Thread(getBuildScheduleRunnable()).start());
        builder.setNegativeButton(R.string.cancel, null);

        builder.setTitle(R.string.build_confirmation_title);
        builder.setMessage(R.string.build_confirmation_summary);

        return builder;
    }
}