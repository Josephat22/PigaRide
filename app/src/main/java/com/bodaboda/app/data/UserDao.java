package com.bodaboda.app.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    long insert(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getByEmail(String email);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    UserEntity getByPhone(String phone);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getById(int id);

    @Query("SELECT COUNT(*) FROM users")
    int countAll();

    @Query("SELECT COUNT(*) FROM users")
    int countPassengers();
}
