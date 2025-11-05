package com.example.eecs4443groupprojectgroup1.Util_Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.eecs4443groupprojectgroup1.Activity.MainActivity;

/**
 * Utility class to handle user sign out.
 */
public class SignOutHelper {

    /**
     * Sign out the user by clearing auto-login data
     * and navigating back to login screen.
     *
     * @param activity Current activity context
     */
    public static void signOut(Activity activity) {
        // Clear saved username to disable auto-login
        activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("autologin", false)
                .remove("username")
                .apply();

        // Navigate back to MainActivity (login screen)
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

        // Close current activity
        activity.finish();
    }
}