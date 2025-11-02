package com.example.eecs4443groupprojectgroup1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

        // Save the current tab as "CHAT"
        SharedPreferencesHelper.saveCurrentTab(requireActivity(), "CHAT");

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
                    Bitmap bitmap = ImageUtil.decodeFromBase64(user.userIcon);
                    if (bitmap != null) {
                        userIcon.setImageBitmap(bitmap);
                    } else {
                        userIcon.setImageResource(R.drawable.user_icon);
                    }
                } else {
                    userIcon.setImageResource(R.drawable.user_icon);
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
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }
}