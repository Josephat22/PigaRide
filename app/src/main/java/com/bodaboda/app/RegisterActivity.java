package com.bodaboda.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bodaboda.app.data.AppDatabase;
import com.bodaboda.app.data.RiderEntity;
import com.bodaboda.app.data.UserEntity;
import com.bodaboda.app.utils.PasswordUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout btnRolePassenger, btnRoleRider, riderFieldsLayout, btnRegister;
    private EditText etName, etPhone, etEmail, etPassword, etLicenseNumber, etVehicleRegNo;
    private ImageView ivTogglePassword;
    private ProgressBar progressBar;
    private Spinner spinnerCountryCode;
    private TextView tvGoToLogin;

    private boolean isRider = false;
    private boolean passwordVisible = false;

    private AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRolePassenger = findViewById(R.id.btnRolePassenger);
        btnRoleRider = findViewById(R.id.btnRoleRider);
        riderFieldsLayout = findViewById(R.id.riderFieldsLayout);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etLicenseNumber = findViewById(R.id.etLicenseNumber);
        etVehicleRegNo = findViewById(R.id.etVehicleRegNo);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.country_codes,
                android.R.layout.simple_spinner_item
        );
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(countryAdapter);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        db = AppDatabase.getInstance(this);

        btnRolePassenger.setOnClickListener(v -> setRole(false));
        btnRoleRider.setOnClickListener(v -> setRole(true));

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setRole(boolean rider) {
        isRider = rider;
        if (rider) {
            btnRolePassenger.setBackgroundResource(R.drawable.bg_role_pill_unselected);
            btnRoleRider.setBackgroundResource(R.drawable.bg_role_pill_selected);
            riderFieldsLayout.setVisibility(View.VISIBLE);
        } else {
            btnRolePassenger.setBackgroundResource(R.drawable.bg_role_pill_selected);
            btnRoleRider.setBackgroundResource(R.drawable.bg_role_pill_unselected);
            riderFieldsLayout.setVisibility(View.GONE);
        }
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

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String phoneLocal = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneLocal) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = "+256" + phoneLocal.replace(" ", "");
        // If email left blank, fall back to a placeholder unique value so it can still be used as a login key.
        String finalEmail = TextUtils.isEmpty(email) ? phone + "@bodaboda.local" : email;

        String licenseNumber = "", vehicleRegNo = "";
        if (isRider) {
            licenseNumber = etLicenseNumber.getText().toString().trim();
            vehicleRegNo = etVehicleRegNo.getText().toString().trim();
            if (TextUtils.isEmpty(licenseNumber) || TextUtils.isEmpty(vehicleRegNo)) {
                Toast.makeText(this, "Please fill in rider details", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        setLoading(true);
        String hashedPassword = PasswordUtil.hash(password);
        String finalLicenseNumber = licenseNumber;
        String finalVehicleRegNo = vehicleRegNo;
        String finalPhone = phone;

        executor.execute(() -> {
            if (db.userDao().getByEmail(finalEmail) != null || db.riderDao().getByEmail(finalEmail) != null) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(this, "An account with this email/phone already exists", Toast.LENGTH_LONG).show();
                });
                return;
            }

            if (isRider) {
                RiderEntity rider = new RiderEntity();
                rider.name = name;
                rider.email = finalEmail;
                rider.phone = finalPhone;
                rider.passwordHash = hashedPassword;
                rider.licenseNumber = finalLicenseNumber;
                rider.vehicleRegNo = finalVehicleRegNo;
                rider.createdAt = System.currentTimeMillis();
                db.riderDao().insert(rider);
                runOnUiThread(() -> onRegisterSuccess("Registered! Awaiting admin approval."));
            } else {
                UserEntity user = new UserEntity();
                user.name = name;
                user.email = finalEmail;
                user.phone = finalPhone;
                user.passwordHash = hashedPassword;
                user.createdAt = System.currentTimeMillis();
                db.userDao().insert(user);
                runOnUiThread(() -> onRegisterSuccess("Registration successful!"));
            }
        });
    }

    private void onRegisterSuccess(String message) {
        setLoading(false);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!loading);
        btnRegister.setAlpha(loading ? 0.6f : 1f);
    }
}
