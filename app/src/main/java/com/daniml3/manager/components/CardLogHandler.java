package com.daniml3.manager.components;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.daniml3.manager.NetUtils;
import com.daniml3.manager.R;
import com.daniml3.manager.TextUtils;
import com.daniml3.manager.extensions.AnimatedButton;
import com.daniml3.manager.extensions.ExpandableCardView;

import org.json.JSONException;
import org.json.JSONObject;

public class CardLogHandler {

    private final int LOADING_ANIMATION_DURATION = 1000;
    private final int LAYOUT_CHANGE_DURATION = 100;

    private final int mLogLineCount;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final TextView mLogContainer;

    private final ViewAnimator mViewAnimator;

    private final AnimatedButton mRefreshButton;

    public CardLogHandler(ExpandableCardView expandableCardView, JSONObject jobInfo, int logLineCount) {
        mLogLineCount = logLineCount;

        mLogContainer = expandableCardView.findViewById(R.id.log_textview);
        mRefreshButton = expandableCardView.findViewById(R.id.refresh_button);
        mViewAnimator = new ViewAnimator(mLogContainer);

        expandableCardView.setOnExpandListener(() -> new Thread(() -> {
            String log;
            try {
                mHandler.post(() -> {
                    mLogContainer.setText(R.string.fetching_log);
                    mViewAnimator.startLoadingAnimation(LOADING_ANIMATION_DURATION);
                    mViewAnimator.startSmoothLayoutChange(LAYOUT_CHANGE_DURATION);
                    mRefreshButton.setVisibility(View.VISIBLE);

                    mRefreshButton.setOnClickListener(expandableCardView::callOnExpand);
                });

                log = NetUtils.getResponse(
                        jobInfo.getString("url") + "/consoleText");

                if (log == null) {
                    onError();
                    return;
                }

                log = TextUtils.getLastLines(log, mLogLineCount);

                String finalLog = log;
                mHandler.post(() -> {
                    mViewAnimator.startSmoothLayoutChange(LAYOUT_CHANGE_DURATION);
                    mLogContainer.setText(finalLog);
                    mViewAnimator.stopLoadingAnimation();
                });
            } catch (JSONException e) {
                onError();
                e.printStackTrace();
            }
        }).start(), true);

        expandableCardView.setOnCollapseListener(() -> {
            mLogContainer.setText("");
            mViewAnimator.stopLoadingAnimation();
            mViewAnimator.startSmoothLayoutChange(LAYOUT_CHANGE_DURATION);
            mRefreshButton.setVisibility(View.GONE);
        }, false);
    }

    private void onError() {
        mHandler.post(() -> {
            mLogContainer.setText(R.string.failed_to_fetch_log);
            mViewAnimator.stopLoadingAnimation();
        });
    }
}
