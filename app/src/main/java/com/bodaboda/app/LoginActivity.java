package com.bodaboda.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bodaboda.app.admin.AdminDashboardActivity;
import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;
import com.bodaboda.app.data.UserEntity;
import com.bodaboda.app.passenger.PassengerDashboardActivity;
import com.bodaboda.app.rider.RiderDashboardActivity;
import com.bodaboda.app.utils.PasswordUtil;
import com.bodaboda.app.utils.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String ADMIN_EMAIL = "admin@bodaboda.com";
    private static final String ADMIN_PASSWORD = "admin123";

    private EditText etIdentifier, etPassword;
    private ImageView ivTogglePassword;
    private LinearLayout btnLogin;
    private ProgressBar progressBar;
    private TextView tvGoToRegister, tvForgotPassword;

    private boolean passwordVisible = false;

    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etIdentifier = findViewById(R.id.etIdentifier);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Please contact an administrator to reset your password.", Toast.LENGTH_LONG).show());
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_visibility);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String identifier = etIdentifier.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(identifier) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your phone/email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (identifier.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            session.saveSession(0, "Administrator", ADMIN_EMAIL, "admin");
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
            return;
        }

        setLoading(true);
        String hashedPassword = PasswordUtil.hash(password);
        boolean looksLikeEmail = identifier.contains("@");

        String normalizedPhone = null;
        if (!looksLikeEmail) {
            String digitsOnly = identifier.replaceAll("[^0-9+]", "");
            normalizedPhone = digitsOnly.startsWith("+") ? digitsOnly : "+256" + digitsOnly;
        }
        final String phoneToTry = normalizedPhone;

        executor.execute(() -> {
            RiderEntity rider = looksLikeEmail
                    ? db.riderDao().getByEmail(identifier)
                    : db.riderDao().getByPhone(phoneToTry);

            if (rider != null && rider.passwordHash.equals(hashedPassword)) {
                session.saveSession(rider.id, rider.name, rider.email, "rider");
                runOnUiThread(() -> goToDashboard(RiderDashboardActivity.class));
                return;
            }

            UserEntity user = looksLikeEmail
                    ? db.userDao().getByEmail(identifier)
                    : db.userDao().getByPhone(phoneToTry);

            if (user != null && user.passwordHash.equals(hashedPassword)) {
                session.saveSession(user.id, user.name, user.email, "passenger");
                runOnUiThread(() -> goToDashboard(PassengerDashboardActivity.class));
                return;
            }

            runOnUiThread(() -> {
                setLoading(false);
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show();
            });
        });
    }

    private void goToDashboard(Class<?> activityClass) {
        setLoading(false);
        startActivity(new Intent(this, activityClass));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnLogin.setAlpha(loading ? 0.6f : 1f);
    }
}
