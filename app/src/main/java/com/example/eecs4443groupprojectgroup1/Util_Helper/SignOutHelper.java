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
        // Clear auto-login data and userId from SharedPreferences using SharedPreferencesHelper
        SharedPreferencesHelper.saveAutoLogin(activity, false);  // Disable auto-login
        SharedPreferencesHelper.saveUserId(activity, -1);  // Clear user ID

        // Navigate back to MainActivity (login screen)
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

        // Close the current activity so that user cannot return to the previous screen
        activity.finish();
    }
}