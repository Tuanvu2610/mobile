package com.example.bansach.Activity;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";
    private static final String KEY_STATUS = "status";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Lưu thông tin đăng nhập
    public void saveLogin(String userId, String username, String role, String status) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy username
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    // Lấy user_id
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    // Lấy role
    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    // Xóa session khi đăng xuất
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
