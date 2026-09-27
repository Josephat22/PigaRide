package com.bodaboda.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bodaboda.app.utils.PasswordUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserEntity.class, RiderEntity.class, TripEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract RiderDao riderDao();
    public abstract TripDao tripDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "bodaboda_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public void seedAdminIfNeeded() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UserEntity admin = userDao().getByEmail("admin@bodaboda.com");
            if (admin == null) {
                UserEntity newAdmin = new UserEntity();
                newAdmin.name = "Admin";
                newAdmin.email = "admin@bodaboda.com";
                newAdmin.phone = "0700000000";
                newAdmin.passwordHash = PasswordUtil.hash("admin123");
                newAdmin.role = UserEntity.ROLE_ADMIN;
                newAdmin.createdAt = System.currentTimeMillis();
                userDao().insert(newAdmin);
            }
        });
    }
}