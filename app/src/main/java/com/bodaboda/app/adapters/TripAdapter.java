package com.bodaboda.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bodaboda.app.R;
import com.bodaboda.app.data.TripEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private final List<TripEntity> trips;
    private final boolean showRiderName;

    public TripAdapter(List<TripEntity> trips, boolean showRiderName) {
        this.trips = trips;
        this.showRiderName = showRiderName;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripEntity trip = trips.get(position);

        String otherParty = showRiderName
                ? (trip.riderName != null ? "Rider: " + trip.riderName : "Rider: not yet assigned")
                : "Passenger: " + trip.passengerName;

        holder.tvTripRoute.setText(otherParty + "\n" + trip.pickupAddress + " -> " + trip.dropoffAddress);
        holder.tvTripFare.setText(String.format(Locale.getDefault(), "Fare: KES %.0f  |  %.1f km", trip.fare, trip.distanceKm));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        holder.tvTripStatus.setText("Status: " + trip.status + "  -  " + sdf.format(trip.timestamp));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvTripRoute, tvTripFare, tvTripStatus;

        TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripRoute = itemView.findViewById(R.id.tvTripRoute);
            tvTripFare = itemView.findViewById(R.id.tvTripFare);
            tvTripStatus = itemView.findViewById(R.id.tvTripStatus);
        }
    }
}
