package com.bodaboda.app.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    public static final String ROLE_PASSENGER = "passenger";
    public static final String ROLE_ADMIN = "admin";

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String email;
    public String phone;
    public String passwordHash;
    public String role = ROLE_PASSENGER;
    public long createdAt;
}