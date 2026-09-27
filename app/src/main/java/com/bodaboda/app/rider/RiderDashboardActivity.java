package com.bodaboda.app.rider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bodaboda.app.AboutActivity;
import com.bodaboda.app.LoginActivity;
import com.bodaboda.app.R;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;
import com.bodaboda.app.data.TripEntity;
import com.bodaboda.app.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RiderDashboardActivity extends AppCompatActivity {

    private TextView tvAvailability, tvRequestDetails;
    private Switch switchAvailability;
    private CardView cardIncomingRequest;
    private DrawerLayout drawerLayout;

    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TripEntity currentRequest;
    private RiderEntity rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        tvAvailability = findViewById(R.id.tvAvailability);
        tvRequestDetails = findViewById(R.id.tvRequestDetails);
        switchAvailability = findViewById(R.id.switchAvailability);
        cardIncomingRequest = findViewById(R.id.cardIncomingRequest);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rider Dashboard");
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        ((TextView) headerView.findViewById(R.id.tvHeaderRole)).setText("Rider");

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_trip_history) {
                startActivity(new Intent(this, RiderTripHistoryActivity.class));
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

        findViewById(R.id.btnRefresh).setOnClickListener(v -> checkForRequests());
        findViewById(R.id.btnAccept).setOnClickListener(v -> respondToRequest(true));
        findViewById(R.id.btnDecline).setOnClickListener(v -> respondToRequest(false));

        loadRiderProfile(tvHeaderName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForRequests();
    }

    private void loadRiderProfile(TextView tvHeaderName) {
        executor.execute(() -> {
            rider = db.riderDao().getById(session.getUserId());
            runOnUiThread(() -> {
                if (rider != null) {
                    tvHeaderName.setText(rider.name);
                    boolean online = RiderEntity.STATUS_ONLINE.equals(rider.status);
                    switchAvailability.setOnCheckedChangeListener(null);
                    switchAvailability.setChecked(online);
                    updateAvailabilityLabel(online);
                    switchAvailability.setOnCheckedChangeListener(this::onAvailabilityToggled);
                }
            });
        });
    }

    private void onAvailabilityToggled(CompoundButton buttonView, boolean isOnline) {
        if (rider == null) return;
        rider.status = isOnline ? RiderEntity.STATUS_ONLINE : RiderEntity.STATUS_OFFLINE;
        updateAvailabilityLabel(isOnline);
        executor.execute(() -> db.riderDao().update(rider));
    }

    private void updateAvailabilityLabel(boolean online) {
        tvAvailability.setText(online ? R.string.available : R.string.unavailable);
    }

    private void checkForRequests() {
        if (session.getUserId() == -1) return;
        executor.execute(() -> {
            TripEntity pending = db.tripDao().getPendingForRider(session.getUserId());
            runOnUiThread(() -> {
                currentRequest = pending;
                if (pending != null) {
                    tvRequestDetails.setText(String.format(
                            "Passenger: %s\n%s -> %s\nFare: KES %.0f (%.1f km)",
                            pending.passengerName, pending.pickupAddress, pending.dropoffAddress,
                            pending.fare, pending.distanceKm));
                    cardIncomingRequest.setVisibility(View.VISIBLE);
                } else {
                    cardIncomingRequest.setVisibility(View.GONE);
                }
            });
        });
    }

    private void respondToRequest(boolean accept) {
        if (currentRequest == null) return;
        currentRequest.status = accept ? TripEntity.STATUS_ACCEPTED : TripEntity.STATUS_DECLINED;
        if (!accept) {
            currentRequest.riderId = null;
            currentRequest.riderName = null;
        }

        executor.execute(() -> {
            db.tripDao().update(currentRequest);
            runOnUiThread(() -> {
                cardIncomingRequest.setVisibility(View.GONE);
                Toast.makeText(this, accept ? "Trip accepted" : "Trip declined", Toast.LENGTH_SHORT).show();
            });
        });
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
