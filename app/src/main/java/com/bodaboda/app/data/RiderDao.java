package com.bodaboda.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RiderDao {

    @Insert
    long insert(RiderEntity rider);

    @Update
    void update(RiderEntity rider);

    @Query("SELECT * FROM riders WHERE email = :email LIMIT 1")
    RiderEntity getByEmail(String email);

    @Query("SELECT * FROM riders WHERE phone = :phone LIMIT 1")
    RiderEntity getByPhone(String phone);

    @Query("SELECT * FROM riders WHERE id = :id LIMIT 1")
    RiderEntity getById(int id);

    @Query("SELECT * FROM riders")
    List<RiderEntity> getAll();

    @Query("SELECT * FROM riders WHERE status = 'online' AND approvalStatus = 'approved' LIMIT 1")
    RiderEntity getFirstAvailable();

    @Query("SELECT COUNT(*) FROM riders")
    int countAll();
}
