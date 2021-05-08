package com.daniml3.manager.components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daniml3.manager.Constants;
import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.extensions.ExpandableCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardService {

    private final int CARD_FADE_DURATION = 250;

    private final int mCardCount;

    private final LinearLayout mCardContainer;

    private final Context mContext;

    private final int mRefreshFrequencyMs;

    private final Handler mHandler;

    private final ArrayList<ExpandableCardView> mBuildInfoCardList;

    private final String TAG = "CardService";

    private JSONArray mJobsInfo;
    private JSONArray mLastJobsInfo;

    private boolean mRunning;
    private boolean mPause;

    private SharedPreferences mSharedPreferences;

    private Thread mServiceThread;

    public CardService(LinearLayout cardContainer, int cardCount, Context context) {
        mCardContainer = cardContainer;
        mCardCount = cardCount;
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(Constants.SETTINGS_PREFERENCES, 0);

        mBuildInfoCardList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper());
        mRefreshFrequencyMs = mSharedPreferences.getInt(Constants.BUILD_CARD_REFRESH_FREQ_PREFERENCE, Constants.BUILD_CARD_REFRESH_FREQ_DEFAULT) * 1000;

    }

    public void init() {
        new Thread(() -> {
            mJobsInfo = new JSONArray();
            mLastJobsInfo = new JSONArray();
            mRunning = true;

            generateAllCards();

            mServiceThread = new Thread(() -> {
                while (mRunning) {
                    try {
                        if (!mPause) {
                            mLastJobsInfo = mJobsInfo;
                            mJobsInfo = Utils.getJobList();

                            if (mJobsInfo.length() == 0) {
                                clearBuildInfoCards();
                            } else if (!mJobsInfo.getJSONObject(0).getString("fullDisplayName")
                                    .equals(mLastJobsInfo.getJSONObject(0).getString("fullDisplayName")) ||
                                    mBuildInfoCardList.isEmpty() || mJobsInfo.length() != mLastJobsInfo.length()) {
                                generateAllCards();
                            } else {
                                if (mJobsInfo.length() != 0) {
                                    for (int i = 0; i < mCardCount && i < mJobsInfo.length(); i++) {
                                        int index = i;

                                        mHandler.post(() -> {
                                            try {
                                                if (!mBuildInfoCardList.isEmpty()) {
                                                    styleCard(mBuildInfoCardList.get(index), mJobsInfo.getJSONObject(index));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                }
                            }

                            Utils.sleep(mRefreshFrequencyMs);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Detected JSONException on main thread, is the network active?");
                    }
                }
            });
            mServiceThread.start();
        }).start();
    }

    public void deInit() {
        mRunning = false;
        if (mServiceThread != null) {
            mHandler.postDelayed(() -> mServiceThread.interrupt(), mRefreshFrequencyMs * 2);
        }
        clearBuildInfoCards();
    }

    public void pause() {
        mPause = true;
    }

    public void resume() {
        mPause = false;
    }

    private void generateAllCards() {
        clearBuildInfoCards();

        mHandler.postDelayed(() -> {
            for (int i = 0; i < mCardCount && i < mJobsInfo.length(); i++) {
                int index = i;
                mHandler.post(() -> {
                    try {
                        generateBuildInfoCard(mJobsInfo.getJSONObject(index));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, CARD_FADE_DURATION * 2);
    }

    private void generateBuildInfoCard(JSONObject buildInfo) throws JSONException {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ExpandableCardView buildInfoCard = (ExpandableCardView) inflater.inflate(R.layout.build_info_card, mCardContainer, false);
        String title = buildInfo.getString("fullDisplayName");
        String url = buildInfo.getString("url");

        TextView buildTitle = buildInfoCard.findViewById(R.id.build_title);
        LinearLayout logContainer = buildInfoCard.findViewById(R.id.log_container);

        buildTitle.setText(title);

        buildInfoCard.setExpandableView(logContainer);
        buildInfoCard.setOnLongClickListener(() -> mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));

        buildInfoCard.setAlpha(0f);
        buildInfoCard.animate().alpha(1.0f).setDuration(250).start();
        buildInfoCard.setOnClickListener(() -> {
            for (ExpandableCardView expandableCardView : mBuildInfoCardList) {
                if (expandableCardView.isExpanded() && expandableCardView != buildInfoCard) {
                    expandableCardView.setExpanded(false);
                }
            }
        });

        new CardLogHandler(buildInfoCard, buildInfo, mSharedPreferences.getInt(Constants.LOG_LINE_COUNT_PREFERENCE, Constants.LOG_LINE_COUNT_DEFAULT));

        styleCard(buildInfoCard, buildInfo);

        mBuildInfoCardList.add(buildInfoCard);
        mCardContainer.addView(buildInfoCard);
    }

    private void styleCard(ExpandableCardView buildInfoCard, JSONObject buildInfo) throws JSONException {
        String status = buildInfo.getString("result");

        TextView buildStatus = buildInfoCard.findViewById(R.id.build_status);
        View separator = buildInfoCard.findViewById(R.id.separator);

        if (buildInfo.getBoolean("building")) {
            buildStatus.setText(R.string.build_building);
        } else {
            int color = 0;
            int statusString = 0;

            switch (status.toLowerCase()) {
                case "aborted":
                    color = Color.GRAY;
                    statusString = R.string.build_aborted;
                    break;
                case "success":
                    color = Color.GREEN;
                    statusString = R.string.build_success;
                    break;
                case "failure":
                    statusString = R.string.build_failure;
                    color = Color.RED;
                    break;
            }

            if (color != 0) {
                separator.setBackgroundColor(color);
            }

            if (statusString != 0) {
                buildStatus.setText(statusString);
            } else {
                buildStatus.setText(Utils.firstLetterToUpperCase(status.toLowerCase()));
            }
        }

    }

    private void clearBuildInfoCards() {
        mHandler.post(() -> {
            if (!mBuildInfoCardList.isEmpty()) {
                for (View card : mBuildInfoCardList) {
                    card.animate().alpha(0f).setDuration(CARD_FADE_DURATION).start();
                }

                mHandler.postDelayed(mCardContainer::removeAllViews, CARD_FADE_DURATION);
                mBuildInfoCardList.clear();
            }
        });
    }
}