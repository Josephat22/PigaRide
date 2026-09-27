package com.bodaboda.app.passenger;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bodaboda.app.R;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;
import com.bodaboda.app.data.TripEntity;
import com.bodaboda.app.utils.FareCalculator;
import com.bodaboda.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RideRequestActivity extends AppCompatActivity {

    private EditText etPickup, etDropoff, etDistance;
    private TextView tvFare;
    private Button btnConfirmRequest;

    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_request);

        etPickup = findViewById(R.id.etPickup);
        etDropoff = findViewById(R.id.etDropoff);
        etDistance = findViewById(R.id.etDistance);
        tvFare = findViewById(R.id.tvFare);
        btnConfirmRequest = findViewById(R.id.btnConfirmRequest);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

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

        btnConfirmRequest.setOnClickListener(v -> createRideRequest());
    }

    private void createRideRequest() {
        String pickup = etPickup.getText().toString().trim();
        String dropoff = etDropoff.getText().toString().trim();
        String distanceStr = etDistance.getText().toString().trim();

        if (TextUtils.isEmpty(pickup) || TextUtils.isEmpty(dropoff) || TextUtils.isEmpty(distanceStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                finish();
            });
        });
    }
}
