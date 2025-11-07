package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SignOutHelper;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;

public class SettingsFragment extends Fragment {

    private UserViewModel userViewModel;

    private ImageView userIcon;
    private TextView userName;
    private TextView userDescription;
    private LinearLayout signOutLayout;
    private LinearLayout settingsLayout;

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
        signOutLayout = view.findViewById(R.id.signout);
        settingsLayout = view.findViewById(R.id.settings);

        // Get the current user's ID from SharedPreferences using SharedPreferencesHelper
        int currentUserId = SharedPreferencesHelper.getUserId(requireActivity());

        if (currentUserId == -1) {
            // If userId is not found, show default values
            userName.setText("Name");
            userDescription.setText("User has no description.");
            userIcon.setImageResource(R.drawable.user_icon);
            return view;
        }

        // Save the current tab as "CHAT" using SharedPreferencesHelper
        SharedPreferencesHelper.saveCurrentTab(requireActivity(), "CHAT");

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Observe user data by userId
        userViewModel.getUserById(currentUserId).observe(getViewLifecycleOwner(), user -> {
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

        // Navigate to ProfileActivity
        settingsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }
}