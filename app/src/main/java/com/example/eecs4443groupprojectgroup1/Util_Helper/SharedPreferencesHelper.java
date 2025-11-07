package com.example.eecs4443groupprojectgroup1.Util_Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "HomeActivityPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_AUTO_LOGIN = "autoLogin"; // Key for auto-login state

    // Save the current tab in SharedPreferences
    public static void saveCurrentTab(Context context, String tab) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_TAB, tab);
        editor.apply();
    }

    // Save the user ID in SharedPreferences
    public static void saveUserId(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Save auto-login state in SharedPreferences
    public static void saveAutoLogin(Context context, boolean autoLogin) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AUTO_LOGIN, autoLogin);
        editor.apply();
    }

    // Retrieve the user ID from SharedPreferences
    public static int getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, -1); // Return -1 if userId is not found
    }
}