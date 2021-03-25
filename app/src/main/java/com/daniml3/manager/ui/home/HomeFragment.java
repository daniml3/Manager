package com.daniml3.manager.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.daniml3.manager.R;
import com.daniml3.manager.Utils;
import com.daniml3.manager.extensions.AnimatedButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private boolean mIsGettingServerStatus;
    private Context mContext;
    private Activity mActivity;

    private TextView mStatus;
    private TextView mStatusDescription;

    private DrawerLayout drawer;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mContext = context;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnimatedButton mRefreshButton = view.findViewById(R.id.action_button);

        mStatus = view.findViewById(R.id.status);
        mStatusDescription = view.findViewById(R.id.status_description);
        drawer = mActivity.findViewById(R.id.drawer_layout);

        mRefreshButton.setOnClickListener(this::updateServerStatus);
        mRefreshButton.setOnLongClickListener(() -> {
            Utils.vibrate(mContext);
            drawer.open();
        });

        CardView mServerInformationCard = view.findViewById(R.id.status_card);
        mServerInformationCard.setOnLongClickListener(v -> {
            Utils.vibrate(mContext);
            return true;
        });

        updateServerStatus();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void updateServerStatus() {
        if (mIsGettingServerStatus) {
            return;
        }

        mIsGettingServerStatus = true;

        new Thread(() -> {
            mActivity.runOnUiThread(() -> {
                showSnackBar(getString(R.string.getting_status));

                mStatus.setText(R.string.getting_status);
                mStatusDescription.setVisibility(View.GONE);
            });


            JSONObject response = Utils.getServerAvailableResponse();
            boolean isServerAvailable = Utils.isServerAvailable();

            if (response != null) {
                try {
                    response.getString("displayName");

                    String totalExecutors = String.valueOf(response.getInt("totalExecutors"));
                    String busyExecutors = String.valueOf(response.getInt("busyExecutors"));
                    mActivity.runOnUiThread(() -> {
                        mStatus.setText(R.string.server_available);
                        mStatusDescription.setVisibility(View.VISIBLE);
                        mStatusDescription.setText(
                                String.format(mActivity.getString(R.string.server_information), totalExecutors, busyExecutors));
                    });
                } catch (JSONException e) {
                    isServerAvailable = false;
                }
            }

            if (!isServerAvailable) {
                mActivity.runOnUiThread(() -> mStatus.setText(R.string.server_unavailable));
            }

            mIsGettingServerStatus = false;
        }).start();
    }

    private void showSnackBar(String content) {
        Snackbar.make(mActivity.findViewById(R.id.home_snack_bar_placeholder), content, Snackbar.LENGTH_SHORT).show();
    }
}