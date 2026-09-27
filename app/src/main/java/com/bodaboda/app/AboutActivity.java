package com.bodaboda.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.flexbox.FlexboxLayout;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.about_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        setFeatureRow(R.id.rowRoleBased, R.drawable.ic_people, R.string.feature_role_based);
        setFeatureRow(R.id.rowOffline, R.drawable.ic_offline, R.string.feature_offline);
        setFeatureRow(R.id.rowVerification, R.drawable.ic_shield_check, R.string.feature_verification);
        setFeatureRow(R.id.rowFare, R.drawable.ic_calculator, R.string.feature_fare);
        setFeatureRow(R.id.rowHistory, R.drawable.ic_history, R.string.feature_history);

        findViewById(R.id.rowEmail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + getString(R.string.developer_email)));
            startActivity(intent);
        });

        findViewById(R.id.rowGithub).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/" + getString(R.string.developer_github)));
            startActivity(intent);
        });

        addTechChips();
    }

    private void setFeatureRow(int rowId, int iconRes, int textRes) {
        View row = findViewById(rowId);
        ((ImageView) row.findViewById(R.id.ivFeatureIcon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.tvFeatureText)).setText(textRes);
    }

    private void addTechChips() {
        FlexboxLayout container = findViewById(R.id.techChipContainer);
        String[] techs = {"Java", "Android Studio", "Room Database", "Material Design"};

        for (String tech : techs) {
            TextView chip = new TextView(this);
            chip.setText(tech);
            chip.setTextColor(getResources().getColor(R.color.text_white_soft));
            chip.setTextSize(13f);
            chip.setBackgroundResource(R.drawable.bg_tech_chip);
            chip.setPadding(36, 20, 36, 20);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 16);
            chip.setLayoutParams(params);

            container.addView(chip);
        }
    }
}
