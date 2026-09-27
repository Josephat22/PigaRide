package com.bodaboda.app.rider;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bodaboda.app.R;
import com.bodaboda.app.adapters.TripAdapter;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.TripEntity;
import com.bodaboda.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RiderTripHistoryActivity extends AppCompatActivity {

    private final List<TripEntity> tripList = new ArrayList<>();
    private TripAdapter adapter;
    private TextView tvEmpty;

    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        RecyclerView rvTrips = findViewById(R.id.rvTrips);
        tvEmpty = findViewById(R.id.tvEmpty);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        adapter = new TripAdapter(tripList, false);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));
        rvTrips.setAdapter(adapter);

        loadTrips();
    }

    private void loadTrips() {
        executor.execute(() -> {
            List<TripEntity> result = db.tripDao().getByRider(session.getUserId());
            runOnUiThread(() -> {
                tripList.clear();
                tripList.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(tripList.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
