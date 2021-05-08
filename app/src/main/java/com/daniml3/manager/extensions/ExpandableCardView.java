package com.daniml3.manager.extensions;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.daniml3.manager.Utils;
import com.daniml3.manager.components.ViewAnimator;

public class ExpandableCardView extends CardView
        implements View.OnTouchListener, View.OnLongClickListener {

    private final String TAG = "ExpandableCardView";

    private final ViewAnimator mViewAnimator;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final int LONG_CLICK_DURATION = 500;

    private boolean mExpanded;
    private boolean mBlockOnClick;
    private boolean mBeforeExpand;
    private boolean mBeforeCollapse;

    private View mExpandableView;

    private Runnable mOnExpand;
    private Runnable mOnCollapse;
    private Runnable mOnClickListener;
    private Runnable mOnLongClickListener;

    private int mExpandDuration = 100;

    public ExpandableCardView(Context context) {
        this(context, null);
    }

    public ExpandableCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mViewAnimator = new ViewAnimator(this);

        setClickable(true);
        setOnTouchListener(this);
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        deprecate("setOnClickListener(OnClickListener listener)",
                "setOnClickListener(Runnable runnable)");
    }

    public void setOnClickListener(Runnable runnable) {
        if (runnable == null) {
            clearOnClickListener();
        } else {
            mOnClickListener = runnable;
        }
    }

    public void clearOnClickListener() {
        mOnClickListener = () -> {
        };
    }

    @Override
    public boolean onLongClick(View view) {
        deprecate("onLongClick(View view)", "callOnLongClick()");
        return true;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        deprecate("setOnLongClickListener(OnLongClickListener listener)",
                "setOnLongClickListener(Runnable runnable");
    }

    public void setOnLongClickListener(Runnable runnable) {
        mOnLongClickListener = () -> {
            runnable.run();
            Utils.vibrate(getContext());
            mBlockOnClick = true;
        };
    }

    public View getExpandableView() {
        return mExpandableView;
    }

    public void setExpandableView(View view) {
        mExpandableView = view;
        setExpanded(mExpanded);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
        if (mExpanded && mBeforeExpand) {
            callOnExpand();
        } else if (mBeforeCollapse) {
            callOnCollapse();
        }

        mViewAnimator.startSmoothLayoutChange(mExpandDuration);
        mExpandableView.setVisibility(mExpanded ? View.VISIBLE : View.GONE);

        if (mExpanded && !mBeforeExpand) {
            callOnExpand();
        } else if (!mBeforeCollapse) {
            callOnCollapse();
        }
    }

    public void setOnExpandListener(Runnable runnable, boolean beforeExpand) {
        mOnExpand = runnable;
        mBeforeExpand = beforeExpand;
    }

    public void setOnCollapseListener(Runnable runnable, boolean beforeCollapse) {
        mOnCollapse = runnable;
        mBeforeCollapse = beforeCollapse;
    }

    @Override
    public boolean callOnClick() {
        return callRunnable(mOnClickListener);
    }

    public void callOnLongClick() {
        callRunnable(mOnLongClickListener);
    }

    public void callOnExpand() {
        callRunnable(mOnExpand);
    }

    public void callOnCollapse() {
        callRunnable(mOnCollapse);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setPressed(true);
            mBlockOnClick = false;
            mHandler.postDelayed(mOnLongClickListener, LONG_CLICK_DURATION);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setPressed(false);
            if (!mBlockOnClick) {
                setExpanded(!mExpanded);
                callOnClick();
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            setPressed(false);
        }

        mHandler.removeCallbacks(mOnLongClickListener);

        return true;
    }

    public void setExpandAnimationDuration(int duration) {
        mExpandDuration = duration;
    }

    private boolean callRunnable(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
            return true;
        }

        return false;
    }

    private void deprecate(String deprecation, String alternative) {
        Log.e(TAG, String.format("%s is deprecated, please use %s instead", deprecation, alternative));
        throw new RuntimeException();
    }
}
