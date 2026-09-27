package com.bodaboda.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "riders")
public class RiderEntity {

    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_OFFLINE = "offline";

    public static final String APPROVAL_PENDING = "pending";
    public static final String APPROVAL_APPROVED = "approved";
    public static final String APPROVAL_SUSPENDED = "suspended";

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String phone;
    public String passwordHash;
    public String licenseNumber;
    public String vehicleRegNo;
    public String status = STATUS_OFFLINE;
    public String approvalStatus = APPROVAL_PENDING;
    public double rating = 5.0;
    public long createdAt;
}
