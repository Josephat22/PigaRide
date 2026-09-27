package com.bodaboda.app.passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bodaboda.app.AboutActivity;
import com.bodaboda.app.LoginActivity;
import com.bodaboda.app.R;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;
import com.bodaboda.app.data.TripEntity;
import com.bodaboda.app.utils.FareCalculator;
import com.bodaboda.app.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PassengerDashboardActivity extends AppCompatActivity {

    private SessionManager session;
    private DrawerLayout drawerLayout;
    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private EditText etDropoff, etDistance;
    private TextView tvFare;

    private View cardLastTrip;
    private TextView tvLastTripRoute, tvLastTripTime, tvNoLastTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_dashboard);

        session = new SessionManager(this);
        db = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Passenger dashboard");
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.tvHeaderName)).setText(session.getName());
        ((TextView) headerView.findViewById(R.id.tvHeaderRole)).setText("Passenger");

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_trip_history) {
                startActivity(new Intent(this, TripHistoryActivity.class));
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

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hi, " + firstNameOf(session.getName()));

        etDropoff = findViewById(R.id.etDropoff);
        etDistance = findViewById(R.id.etDistance);
        tvFare = findViewById(R.id.tvFare);

        etDistance.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                try {
                    double km = Double.parseDouble(s.toString());
                    tvFare.setText(String.format("Estimated fare: KES %.0f", FareCalculator.estimateFare(km)));
                } catch (NumberFormatException e) {
                    tvFare.setText(R.string.estimated_fare);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        findViewById(R.id.btnConfirmRequest).setOnClickListener(v -> createRideRequest());

        findViewById(R.id.btnTripHistory).setOnClickListener(v ->
                startActivity(new Intent(this, TripHistoryActivity.class)));

        cardLastTrip = findViewById(R.id.cardLastTrip);
        tvLastTripRoute = findViewById(R.id.tvLastTripRoute);
        tvLastTripTime = findViewById(R.id.tvLastTripTime);
        tvNoLastTrip = findViewById(R.id.tvNoLastTrip);

        cardLastTrip.setOnClickListener(v ->
                startActivity(new Intent(this, TripHistoryActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLastTrip();
    }

    private String firstNameOf(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "there";
        return fullName.trim().split("\\s+")[0];
    }

    private void loadLastTrip() {
        executor.execute(() -> {
            List<TripEntity> trips = db.tripDao().getByPassenger(session.getUserId());
            runOnUiThread(() -> {
                if (trips.isEmpty()) {
                    cardLastTrip.setVisibility(View.GONE);
                    tvNoLastTrip.setVisibility(View.VISIBLE);
                } else {
                    TripEntity last = trips.get(0);
                    tvLastTripRoute.setText(last.pickupAddress + " to " + last.dropoffAddress);
                    tvLastTripTime.setText(formatRelativeTime(last.timestamp));
                    cardLastTrip.setVisibility(View.VISIBLE);
                    tvNoLastTrip.setVisibility(View.GONE);
                }
            });
        });
    }

    private String formatRelativeTime(long timestamp) {
        Calendar tripCal = Calendar.getInstance();
        tripCal.setTimeInMillis(timestamp);
        Calendar now = Calendar.getInstance();

        boolean sameDay = now.get(Calendar.YEAR) == tripCal.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == tripCal.get(Calendar.DAY_OF_YEAR);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = yesterday.get(Calendar.YEAR) == tripCal.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == tripCal.get(Calendar.DAY_OF_YEAR);

        SimpleDateFormat timeFmt = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String time = timeFmt.format(tripCal.getTime());

        if (sameDay) return "Today, " + time;
        if (isYesterday) return "Yesterday, " + time;

        SimpleDateFormat fullFmt = new SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault());
        return fullFmt.format(tripCal.getTime());
    }

    private void createRideRequest() {
        String pickup = "Current location";
        String dropoff = etDropoff.getText().toString().trim();
        String distanceStr = etDistance.getText().toString().trim();

        if (TextUtils.isEmpty(dropoff) || TextUtils.isEmpty(distanceStr)) {
            Toast.makeText(this, "Enter a destination and distance", Toast.LENGTH_SHORT).show();
            return;
        }

        double distanceKm;
        try {
            distanceKm = Double.parseDouble(distanceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid distance", Toast.LENGTH_SHORT).show();
            return;
        }

        double fare = FareCalculator.estimateFare(distanceKm);

        TripEntity trip = new TripEntity();
        trip.passengerId = session.getUserId();
        trip.passengerName = session.getName();
        trip.pickupAddress = pickup;
        trip.dropoffAddress = dropoff;
        trip.distanceKm = distanceKm;
        trip.fare = fare;
        trip.timestamp = System.currentTimeMillis();

        executor.execute(() -> {
            RiderEntity availableRider = db.riderDao().getFirstAvailable();
            if (availableRider != null) {
                trip.riderId = availableRider.id;
                trip.riderName = availableRider.name;
            }
            db.tripDao().insert(trip);

            runOnUiThread(() -> {
                String message = availableRider != null
                        ? "Ride requested! Waiting for " + availableRider.name + " to accept."
                        : "Ride saved, but no riders are online right now.";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                etDropoff.setText("");
                etDistance.setText("");
                loadLastTrip();
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
