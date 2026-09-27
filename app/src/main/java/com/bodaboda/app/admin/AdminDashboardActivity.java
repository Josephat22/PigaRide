package com.bodaboda.app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bodaboda.app.AboutActivity;
import com.bodaboda.app.LoginActivity;
import com.bodaboda.app.R;
import com.bodaboda.app.adapters.RecentTripAdapter;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.TripEntity;
import com.bodaboda.app.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDashboardActivity extends AppCompatActivity {

    private AppDatabase db;
    private DrawerLayout drawerLayout;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final List<TripEntity> recentTrips = new ArrayList<>();
    private RecentTripAdapter recentTripAdapter;
    private TextView tvNoTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = AppDatabase.getInstance(this);
        SessionManager session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.admin_dashboard_title);
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.tvHeaderName)).setText(session.getName());
        ((TextView) headerView.findViewById(R.id.tvHeaderRole)).setText("Administrator");

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_manage_riders) {
                startActivity(new Intent(this, ManageRidersActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (id == R.id.nav_logout) {
                session.clearSession();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        TextView tvTotalRiders = findViewById(R.id.tvTotalRiders);
        TextView tvTotalTrips = findViewById(R.id.tvTotalTrips);
        tvNoTrips = findViewById(R.id.tvNoTrips);

        RecyclerView rvRecentTrips = findViewById(R.id.rvRecentTrips);
        recentTripAdapter = new RecentTripAdapter(recentTrips);
        rvRecentTrips.setLayoutManager(new LinearLayoutManager(this));
        rvRecentTrips.setAdapter(recentTripAdapter);

        executor.execute(() -> {
            int riderCount = db.riderDao().countAll();
            int tripCount = db.tripDao().countAll();
            List<TripEntity> recent = db.tripDao().getRecent(5);

            runOnUiThread(() -> {
                tvTotalRiders.setText(String.valueOf(riderCount));
                tvTotalTrips.setText(String.valueOf(tripCount));

                recentTrips.clear();
                recentTrips.addAll(recent);
                recentTripAdapter.notifyDataSetChanged();
                tvNoTrips.setVisibility(recentTrips.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });

        findViewById(R.id.btnManageRiders).setOnClickListener(v ->
                startActivity(new Intent(this, ManageRidersActivity.class)));

        findViewById(R.id.tvViewAll).setOnClickListener(v ->
                startActivity(new Intent(this, ManageRidersActivity.class)));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
