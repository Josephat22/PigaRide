package com.bodaboda.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.bodaboda.app.admin.AdminDashboardActivity;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.passenger.PassengerDashboardActivity;
import com.bodaboda.app.rider.RiderDashboardActivity;
import com.bodaboda.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Seed admin account on first launch
        AppDatabase.getInstance(this).seedAdminIfNeeded();

        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            goTo(LoginActivity.class);
            return;
        }

        switch (session.getRole()) {
            case "rider":
                goTo(RiderDashboardActivity.class);
                break;
            case "admin":
                goTo(AdminDashboardActivity.class);
                break;
            default:
                goTo(PassengerDashboardActivity.class);
        }
    }

    private void goTo(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
        finish();
    }
}