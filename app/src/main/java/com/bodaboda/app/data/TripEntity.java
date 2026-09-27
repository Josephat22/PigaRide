package com.bodaboda.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trips")
public class TripEntity {

    public static final String STATUS_REQUESTED = "requested";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_DECLINED = "declined";
    public static final String STATUS_COMPLETED = "completed";

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int passengerId;
    public String passengerName;
    public Integer riderId;
    public String riderName;

    public String pickupAddress;
    public String dropoffAddress;
    public double distanceKm;
    public double fare;
    public String status = STATUS_REQUESTED;
    public long timestamp;
}
