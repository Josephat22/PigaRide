package com.bodaboda.app.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bodaboda.app.R;
import com.bodaboda.app.data.TripEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecentTripAdapter extends RecyclerView.Adapter<RecentTripAdapter.TripViewHolder> {

    private final List<TripEntity> trips;

    public RecentTripAdapter(List<TripEntity> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        TripEntity trip = trips.get(position);
        String name = trip.passengerName != null ? trip.passengerName : "Unknown";

        holder.tvAvatar.setText(initialsOf(name));
        holder.tvName.setText(name);
        holder.tvTime.setText(formatRelativeTime(trip.timestamp));

        boolean completed = TripEntity.STATUS_COMPLETED.equals(trip.status);
        String label = completed ? "Completed" : humanize(trip.status);

        holder.tvStatusBadge.setText(label);
        GradientDrawable bg = (GradientDrawable) holder.tvStatusBadge.getBackground().mutate();
        int bgColor = ContextCompat.getColor(holder.itemView.getContext(),
                completed ? R.color.badge_completed_bg : R.color.badge_progress_bg);
        int textColor = ContextCompat.getColor(holder.itemView.getContext(),
                completed ? R.color.badge_completed_text : R.color.badge_progress_text);
        bg.setColor(bgColor);
        holder.tvStatusBadge.setTextColor(textColor);
    }

    private String humanize(String status) {
        if (status == null || status.isEmpty()) return "Unknown";
        if (TripEntity.STATUS_ACCEPTED.equals(status)) return "In progress";
        if (TripEntity.STATUS_REQUESTED.equals(status)) return "Requested";
        if (TripEntity.STATUS_DECLINED.equals(status)) return "Declined";
        return status.substring(0, 1).toUpperCase() + status.substring(1);
    }

    private String initialsOf(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(p.charAt(0)));
        }
        return sb.length() > 0 ? sb.toString() : "?";
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

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvTime, tvStatusBadge;

        TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}
