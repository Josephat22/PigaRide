package com.bodaboda.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripDao {

    @Insert
    long insert(TripEntity trip);

    @Update
    void update(TripEntity trip);

    @Query("SELECT * FROM trips WHERE id = :id LIMIT 1")
    TripEntity getById(int id);

    @Query("SELECT * FROM trips WHERE passengerId = :passengerId ORDER BY timestamp DESC")
    List<TripEntity> getByPassenger(int passengerId);

    @Query("SELECT * FROM trips WHERE riderId = :riderId ORDER BY timestamp DESC")
    List<TripEntity> getByRider(int riderId);

    @Query("SELECT * FROM trips WHERE riderId = :riderId AND status = 'requested' LIMIT 1")
    TripEntity getPendingForRider(int riderId);

    @Query("SELECT COUNT(*) FROM trips")
    int countAll();

    @Query("SELECT * FROM trips ORDER BY timestamp DESC LIMIT :limit")
    List<TripEntity> getRecent(int limit);
}
