package com.daniml3.manager.ui.home;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daniml3.manager.Constants;
import com.daniml3.manager.NetUtils;
import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.extensions.AnimatedButton;
import com.daniml3.manager.extensions.BuildButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.daniml3.manager.Constants.TAG;

public class BuildFragment extends Fragment  {

    private Activity mActivity;
    private Context mContext;

    private AnimatedButton mTriggerBuildButton;

    private String mBuildToken;

    private SharedPreferences sharedPreferences;

    private ArrayList<String> mArgumentList;
    private ArrayList<BuildButton> mBuildButtonList;
    private ArrayList<LinearLayout> mLinearLayoutList;
    private ArrayList<View> mBuildInfoCardList;

    private DrawerLayout drawer;

    private final int FADE_DURATION = 250;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mContext = context;
    }

    @Override
    public void onViewCreated(@NotNull View createdView, Bundle savedInstanceState) {
        // Initialize the lists
        mBuildButtonList = new ArrayList<>();
        mArgumentList = new ArrayList<>();
        mLinearLayoutList = new ArrayList<>();
        mBuildInfoCardList = new ArrayList<>();

        // Initialize the views
        AnimatedButton refreshButton = mActivity.findViewById(R.id.build_action_button);

        sharedPreferences = mContext.getSharedPreferences("fragment_settings", 0);
        mTriggerBuildButton = mActivity.findViewById(R.id.build_button);
        mBuildToken = sharedPreferences.getString("build_token", "");
        drawer = mActivity.findViewById(R.id.drawer_layout);

        // Layout parameters that will be used for the switches
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        refreshButton.setOnClickListener(this::prepareUI);

        refreshButton.setOnLongClickListener(() -> {
            Utils.vibrate(mContext);
            drawer.open();
        });

        generateConfigButtons(layoutParams);

        // Clear the switch list
        mBuildButtonList.clear();

        CardView configCardView = mActivity.findViewById(R.id.build_config_card);
        configCardView.setOnLongClickListener(v -> {
            Utils.vibrate(mContext);
            return true;
        });

        clearLayouts();

        generateConfigButtons(layoutParams);
        prepareUI();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_build, container, false);
    }

    private LinearLayout getLinearLayout(int currentItemCount) {
        if (currentItemCount % 3 == 0){
            LinearLayout linearLayout = new LinearLayout(mContext);

            linearLayout.setPadding(0, 10, 0 ,10);
            linearLayout.setGravity(Gravity.CENTER);

            ((LinearLayout) mActivity.findViewById(R.id.switch_container)).addView(linearLayout);

            mLinearLayoutList.add(linearLayout);
            return linearLayout;
        }

        return mLinearLayoutList.get(mLinearLayoutList.size()-1);
    }

    private void clearLayouts() {
        int[] linearLayoutList = {R.id.switch_container};
        for (int id : linearLayoutList) {
            LinearLayout linearLayout = mActivity.findViewById(id);
            linearLayout.removeAllViews();
        }

        ViewGroup insertPoint = mActivity.findViewById(R.id.build_info_card_container);
        insertPoint.removeAllViews();
    }

    private void showSnackBar(String content) {
        Snackbar.make(mActivity.findViewById(R.id.build_snack_bar_placeholder), content, Snackbar.LENGTH_SHORT).show();
    }

    private Runnable getBuildButtonRunnable() {
        return () -> {
            mArgumentList.clear();

            for (BuildButton buildButton : mBuildButtonList) {
                mArgumentList.add(buildButton.getBuildVariableName());
                mArgumentList.add(buildButton.getCurrentBuildVariableValue());
            }

            Log.d(TAG, mArgumentList.toString());

            if (Utils.getServerAvailableResponse().length() == 0) {
                Log.d(TAG, "Server unavailable");

                mActivity.runOnUiThread(() -> showSnackBar(mActivity.getString(R.string.server_unavailable)));
                return;
            }

            mActivity.runOnUiThread(() -> mTriggerBuildButton.stopLoadingAnimation());
            NetUtils.getJSONResponse(Utils.getSchedulingUrl(
                    sharedPreferences.getString("build_token", ""), mArgumentList));
            mActivity.runOnUiThread(() -> mTriggerBuildButton.stopLoadingAnimation());
        };
    }

    private void prepareUI() {
        mBuildToken = sharedPreferences.getString("build_token", "");

        new Thread(() -> {
            mActivity.runOnUiThread(() -> mTriggerBuildButton.startLoadingAnimation());
            LinearLayout cardContainer = mActivity.findViewById(R.id.build_info_card_container);
            boolean isServerAvailable = Utils.isServerAvailable();
            JSONArray jobList = Utils.getJobList();
            int jobListLength = jobList.length();

            mActivity.runOnUiThread(() -> {
                clearBuildInfoCards(cardContainer);

                if (!isServerAvailable) {
                        mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.button_disabled));
                        mTriggerBuildButton.setText(R.string.server_unavailable);
                        showSnackBar(mActivity.getString(R.string.server_unavailable));
                        mTriggerBuildButton.setOnClickListener(this::prepareUI);
                } else if (!mBuildToken.isEmpty()){
                        mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.blue));
                        mTriggerBuildButton.setText(R.string.menu_build);
                        mTriggerBuildButton.setOnClickListener(() -> new Thread(getBuildButtonRunnable()).start());
                } else {
                        mTriggerBuildButton.setBackgroundTintList(mActivity.getColorStateList(R.color.button_disabled));
                        mTriggerBuildButton.setText(R.string.missing_build_token);
                        mTriggerBuildButton.clearOnClickListener();
                }

                mHandler.postDelayed(() -> {
                    if (isServerAvailable) {
                        for (int i = 0; i < 5 && i < jobListLength; i++) {
                            try {
                                generateBuildInfoCard(jobList.getJSONObject(i), cardContainer);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, FADE_DURATION);

            });

            mActivity.runOnUiThread(() -> mTriggerBuildButton.stopLoadingAnimation());
        }).start();
    }

    /*
    * Generate the buttons
    *
    * The values will be taken from the Constants class
    * All the validation will be executed on the button class
    */
    private void generateConfigButtons(LinearLayout.LayoutParams layoutParams) {
        try {
            mBuildButtonList.clear();
            int i = 0;
            // Generate the switches for the configuration
            for (int[] switchTextList : Constants.TEXTS_FOR_SWITCHES) {
                // Create a new switch
                BuildButton buildButton = new BuildButton(mActivity);
                LinearLayout linearLayout = getLinearLayout(i);

                // Configure the switch with the name and the default checked value.
                buildButton.setBuildVariableName(
                        Constants.BUILD_VARIABLE_PREFIX + Constants.VARIABLE_NAMES_FOR_SWITCHES[i].toUpperCase());
                buildButton.setMaximumState(Constants.SWITCHES_MAX_VALUES[i]);
                buildButton.setColorListForStates(Constants.COLORS_FOR_SWITCHES[i]);
                buildButton.setVariableValueListForValues(Constants.VALUES_FOR_SWITCHES[i]);
                buildButton.setTextListForStates(switchTextList);

                buildButton.setState(Constants.DEFAULT_SWITCHES_VALUES[i]);
                buildButton.setLayoutParams(layoutParams);
                buildButton.validate();

                // Add the switch to the list and to the layout
                mBuildButtonList.add(buildButton);
                linearLayout.addView(buildButton);
                i++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Invalid configuration for the buttons");
            throw new RuntimeException();
        }
    }

    private void generateBuildInfoCard(JSONObject buildInfo, LinearLayout cardContainer) throws JSONException {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buildInfoCard = inflater.inflate(R.layout.build_info_card, cardContainer, false);
        String title = buildInfo.getString("fullDisplayName");
        String status = buildInfo.getString("result");
        String url = buildInfo.getString("url");

        TextView buildTitle = buildInfoCard.findViewById(R.id.build_title);
        TextView buildStatus = buildInfoCard.findViewById(R.id.build_status);
        buildTitle.setText(title);
        if (buildInfo.getBoolean("building")) {
            buildStatus.setText(R.string.building);
        } else {
            buildStatus.setText(status);
        }

        buildInfoCard.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));

        buildInfoCard.setAlpha(0f);
        buildInfoCard.animate().alpha(1.0f).setDuration(250).start();

        mBuildInfoCardList.add(buildInfoCard);
        cardContainer.addView(buildInfoCard);
    }

    private void clearBuildInfoCards(LinearLayout cardContainer) {
        if (!mBuildInfoCardList.isEmpty()) {
            for (View card : mBuildInfoCardList) {
                card.animate().alpha(0.0f).setDuration(FADE_DURATION).start();
            }

            mHandler.postDelayed(cardContainer::removeAllViews, FADE_DURATION);
            mBuildButtonList.clear();
        }
    }
}