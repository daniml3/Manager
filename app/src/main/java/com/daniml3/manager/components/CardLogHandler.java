package com.daniml3.manager.components;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.daniml3.manager.NetUtils;
import com.daniml3.manager.R;
import com.daniml3.manager.TextUtils;
import com.daniml3.manager.extensions.ExpandableCardView;

import org.json.JSONException;
import org.json.JSONObject;

public class CardLogHandler {

    private TextView mLogContainer;
    private ViewAnimator mViewAnimator;

    private final int LOG_LINE_COUNT = 30;

    private final int LOADING_ANIMATION_DURATION = 1000;
    private final int LAYOUT_CHANGE_DURATION = 250;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public CardLogHandler(ExpandableCardView expandableCardView, JSONObject jobInfo) {
        mLogContainer = (TextView) expandableCardView.getExpandableView();
        mViewAnimator = new ViewAnimator(mLogContainer);

        expandableCardView.setOnExpandListener(() -> new Thread(() -> {
            String log;
            try {
                mHandler.post(() -> mLogContainer.setText(R.string.fetching_log));

                log = NetUtils.getResponse(
                        jobInfo.getString("url") + "/consoleText");

                if (log == null) {
                    onError();
                    return;
                }

                log = TextUtils.getLastLines(log, LOG_LINE_COUNT);

                String finalLog = log;
                mHandler.post(() -> {
                    mViewAnimator.startSmoothLayoutChange(LAYOUT_CHANGE_DURATION);
                    mLogContainer.setText(finalLog);
                });
            } catch (JSONException e) {
                onError();
                e.printStackTrace();
            }
        }).start(), true);

        expandableCardView.setOnCollapseListener(() -> {
            mLogContainer.setText("");
        }, false);
    }

    private void onError() {
        mHandler.post(() -> mLogContainer.setText(R.string.failed_to_fetch_log));
    }
}
