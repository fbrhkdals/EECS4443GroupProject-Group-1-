package com.example.eecs4443groupprojectgroup1.Util_Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "HomeActivityPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";

    // Save the current tab in SharedPreferences
    public static void saveCurrentTab(Context context, String tab) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SELECTED_TAB, tab);
        editor.apply();
    }
}