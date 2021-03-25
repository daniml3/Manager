package com.daniml3.manager;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawer;
    private Activity mActivity;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;

        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_build, R.id.nav_settings)
                .setOpenableLayout(mDrawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, navController);

        MenuItem mCheckedItem = mNavigationView.getCheckedItem();
        if (mCheckedItem != null) {
            mCheckedItem.setEnabled(false);
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
            MenuItem checkedItem = mNavigationView.getCheckedItem();
            if (checkedItem != null) {
                mNavigationView.getCheckedItem().setEnabled(true);
            }
            item.setEnabled(false);
            Utils.vibrate(getApplicationContext());
            mDrawer.close();
            return NavigationUI.onNavDestinationSelected(item, Navigation.findNavController(mActivity, R.id.nav_host_fragment))
                    || super.onOptionsItemSelected(item);
        });
    }

    @Override
    public void onBackPressed() {
        MenuItem checked = mNavigationView.getCheckedItem();
        if (checked != null) {
            checked.setEnabled(true);
        }
        Utils.vibrate(getApplicationContext());
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}