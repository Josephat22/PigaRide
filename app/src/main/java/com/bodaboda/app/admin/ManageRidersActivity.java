package com.bodaboda.app.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bodaboda.app.R;
import com.bodaboda.app.adapters.RiderAdapter;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageRidersActivity extends AppCompatActivity implements RiderAdapter.OnRiderActionListener {

    private final List<RiderEntity> allRiders = new ArrayList<>();
    private final List<RiderEntity> displayedRiders = new ArrayList<>();
    private RiderAdapter adapter;

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private TextView chipAll, chipApproved, chipPending, chipSuspended;
    private String activeFilter = "all";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_riders);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.manage_riders);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db = AppDatabase.getInstance(this);

        RecyclerView rvRiders = findViewById(R.id.rvRiders);
        adapter = new RiderAdapter(displayedRiders, this);
        rvRiders.setLayoutManager(new LinearLayoutManager(this));
        rvRiders.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                searchQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        chipAll = findViewById(R.id.chipAll);
        chipApproved = findViewById(R.id.chipApproved);
        chipPending = findViewById(R.id.chipPending);
        chipSuspended = findViewById(R.id.chipSuspended);

        chipAll.setOnClickListener(v -> selectFilter("all"));
        chipApproved.setOnClickListener(v -> selectFilter(RiderEntity.APPROVAL_APPROVED));
        chipPending.setOnClickListener(v -> selectFilter(RiderEntity.APPROVAL_PENDING));
        chipSuspended.setOnClickListener(v -> selectFilter(RiderEntity.APPROVAL_SUSPENDED));

        loadRiders();
    }

    private void selectFilter(String filter) {
        activeFilter = filter;

        TextView[] chips = {chipAll, chipApproved, chipPending, chipSuspended};
        String[] values = {"all", RiderEntity.APPROVAL_APPROVED, RiderEntity.APPROVAL_PENDING, RiderEntity.APPROVAL_SUSPENDED};

        for (int i = 0; i < chips.length; i++) {
            boolean selected = values[i].equals(filter);
            chips[i].setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            chips[i].setTextColor(getResources().getColor(selected ? R.color.text_white : R.color.chip_unselected_text));
        }

        applyFilters();
    }

    private void applyFilters() {
        displayedRiders.clear();
        for (RiderEntity r : allRiders) {
            boolean matchesFilter = "all".equals(activeFilter) || activeFilter.equals(r.approvalStatus);
            boolean matchesSearch = searchQuery.isEmpty()
                    || r.name.toLowerCase(Locale.getDefault()).contains(searchQuery)
                    || (r.vehicleRegNo != null && r.vehicleRegNo.toLowerCase(Locale.getDefault()).contains(searchQuery));
            if (matchesFilter && matchesSearch) {
                displayedRiders.add(r);
            }
        }
        adapter.notifyDataSetChanged();

        chipAll.setText(getString(R.string.filter_all) + " " + allRiders.size());
    }

    private void loadRiders() {
        executor.execute(() -> {
            List<RiderEntity> result = db.riderDao().getAll();
            runOnUiThread(() -> {
                allRiders.clear();
                allRiders.addAll(result);
                applyFilters();
            });
        });
    }

    @Override
    public void onApprove(RiderEntity rider) {
        rider.approvalStatus = RiderEntity.APPROVAL_APPROVED;
        executor.execute(() -> {
            db.riderDao().update(rider);
            runOnUiThread(() -> {
                Toast.makeText(this, rider.name + " approved", Toast.LENGTH_SHORT).show();
                loadRiders();
            });
        });
    }

    @Override
    public void onSuspend(RiderEntity rider) {
        rider.approvalStatus = RiderEntity.APPROVAL_SUSPENDED;
        executor.execute(() -> {
            db.riderDao().update(rider);
            runOnUiThread(() -> {
                Toast.makeText(this, rider.name + " suspended", Toast.LENGTH_SHORT).show();
                loadRiders();
            });
        });
    }
}
