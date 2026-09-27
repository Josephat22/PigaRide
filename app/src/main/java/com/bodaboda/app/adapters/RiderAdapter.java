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
import com.bodaboda.app.data.RiderEntity;

import java.util.List;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.RiderViewHolder> {

    public interface OnRiderActionListener {
        void onApprove(RiderEntity rider);
        void onSuspend(RiderEntity rider);
    }

    private final List<RiderEntity> riders;
    private final OnRiderActionListener listener;

    public RiderAdapter(List<RiderEntity> riders, OnRiderActionListener listener) {
        this.riders = riders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RiderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rider, parent, false);
        return new RiderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiderViewHolder holder, int position) {
        RiderEntity rider = riders.get(position);
        android.content.Context ctx = holder.itemView.getContext();

        holder.tvAvatar.setText(initialsOf(rider.name));
        holder.tvRiderName.setText(rider.name);
        holder.tvRiderDetails.setText(rider.phone);
        holder.tvVehicleRegNo.setText(rider.vehicleRegNo);

        // Approval status badge
        String status = rider.approvalStatus;
        String label = status.substring(0, 1).toUpperCase() + status.substring(1);
        holder.tvRiderApprovalStatus.setText(label);

        int badgeBg, badgeText;
        if (RiderEntity.APPROVAL_APPROVED.equals(status)) {
            badgeBg = R.color.badge_approved_bg;
            badgeText = R.color.badge_approved_text;
        } else if (RiderEntity.APPROVAL_PENDING.equals(status)) {
            badgeBg = R.color.badge_pending_bg;
            badgeText = R.color.badge_pending_text;
        } else {
            badgeBg = R.color.badge_suspended_bg;
            badgeText = R.color.badge_suspended_text;
        }
        GradientDrawable badgeDrawable = (GradientDrawable) holder.tvRiderApprovalStatus.getBackground().mutate();
        badgeDrawable.setColor(ContextCompat.getColor(ctx, badgeBg));
        holder.tvRiderApprovalStatus.setTextColor(ContextCompat.getColor(ctx, badgeText));

        // Action button: Approved/Suspended riders show "Suspend"; Pending riders show "Approve"
        boolean isPending = RiderEntity.APPROVAL_PENDING.equals(status);
        boolean isSuspended = RiderEntity.APPROVAL_SUSPENDED.equals(status);

        if (isPending || isSuspended) {
            holder.btnAction.setText(R.string.approve);
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_outline_green);
            holder.btnAction.setTextColor(ContextCompat.getColor(ctx, R.color.green_success));
            holder.btnAction.setOnClickListener(v -> listener.onApprove(rider));
        } else {
            holder.btnAction.setText(R.string.suspend);
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_outline_red);
            holder.btnAction.setTextColor(ContextCompat.getColor(ctx, R.color.red_error));
            holder.btnAction.setOnClickListener(v -> listener.onSuspend(rider));
        }
    }

    private String initialsOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty() && sb.length() < 2) sb.append(Character.toUpperCase(p.charAt(0)));
        }
        return sb.length() > 0 ? sb.toString() : "?";
    }

    @Override
    public int getItemCount() {
        return riders.size();
    }

    static class RiderViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvRiderName, tvRiderDetails, tvRiderApprovalStatus, tvVehicleRegNo, btnAction;

        RiderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvRiderName = itemView.findViewById(R.id.tvRiderName);
            tvRiderDetails = itemView.findViewById(R.id.tvRiderDetails);
            tvRiderApprovalStatus = itemView.findViewById(R.id.tvRiderApprovalStatus);
            tvVehicleRegNo = itemView.findViewById(R.id.tvVehicleRegNo);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
