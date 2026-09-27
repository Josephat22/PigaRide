package com.bodaboda.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "bodaboda_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(int userId, String name, String email, String role) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_ROLE, role)
                .apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "passenger");
    }

    public boolean isLoggedIn() {
        return getUserId() != -1;
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public boolean isRider() {
        return "rider".equals(getRole());
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}