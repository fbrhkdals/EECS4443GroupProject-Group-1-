package com.example.eecs4443groupprojectgroup1;

import static androidx.core.content.SharedPreferencesKt.edit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

public class SettingsFragment extends Fragment {

    private UserViewModel userViewModel;

    private ImageView userIcon;
    private TextView userName;
    private TextView userDescription;
    private LinearLayout signOutLayout;
    private LinearLayout settingsLayout;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "HomeActivityPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find views
        userIcon = view.findViewById(R.id.user_icon);
        userName = view.findViewById(R.id.user_name);
        userDescription = view.findViewById(R.id.user_description);
        signOutLayout = view.findViewById(R.id.signout); // Sign out button
        settingsLayout = view.findViewById(R.id.settings); // To Settings button

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String currentUsername = sharedPreferences.getString("username", ""); // Guaranteed not null
        saveCurrentTab("CHAT");

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user data by username
        userViewModel.getUserByUsername(currentUsername).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Set name
                userName.setText((user.username != null && !user.username.isEmpty()) ? user.username : "Name");

                // Set description
                userDescription.setText((user.description != null && !user.description.isEmpty()) ? user.description : "User has no description.");

                // Set image
                if (user.userIcon != null && !user.userIcon.isEmpty()) {
                    File imgFile = new File(user.userIcon);
                    if (imgFile.exists()) {
                        userIcon.setImageURI(Uri.fromFile(imgFile));
                    } else {
                        userIcon.setImageResource(R.drawable.user_icon); // XML default fallback
                    }
                } else {
                    userIcon.setImageResource(R.drawable.user_icon); // XML default fallback
                }
            } else {
                // User not found in DB, show defaults
                userName.setText("Name");
                userDescription.setText("User has no description.");
                userIcon.setImageResource(R.drawable.user_icon);
            }
        });

        // Handle sign out
        signOutLayout.setOnClickListener(v -> SignOutHelper.signOut(requireActivity()));

        // Navigate to Settings
        settingsLayout.setOnClickListener(v -> {
            // Save the current tab as "SETTINGS"
            saveCurrentTab("SETTINGS");

            Intent intent = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Save the current tab in SharedPreferences
    private void saveCurrentTab(String tab) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_SELECTED_TAB, tab);
        editor.apply();
    }
}
