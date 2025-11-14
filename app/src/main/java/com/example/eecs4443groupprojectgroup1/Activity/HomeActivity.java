package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout taskbarFriends, taskbarChat, taskbarSettings;
    private View indicatorFriends, indicatorChat, indicatorSettings, searchIcon, createChatIcon;
    private TextView topBarTitle;

    private static final String PREFS_NAME = "HomeActivityPrefs";
    private static final String KEY_SELECTED_TAB = "selected_tab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        initializeViews();
        setDefaultTabAndIndicators();

        // Set up listeners for taskbar icons and actions
        setListeners();
    }

    private void initializeViews() {
        // Initialize taskbar icons
        taskbarFriends = findViewById(R.id.taskbar_friends);
        taskbarChat = findViewById(R.id.taskbar_chat);
        taskbarSettings = findViewById(R.id.taskbar_settings);

        // Initialize top bar and other icons
        topBarTitle = findViewById(R.id.top_bar_title);
        searchIcon = findViewById(R.id.search_icon);
        createChatIcon = findViewById(R.id.CreateChat_icon);

        // Initialize indicators
        indicatorFriends = findViewById(R.id.indicator_friends);
        indicatorChat = findViewById(R.id.indicator_chat);
        indicatorSettings = findViewById(R.id.indicator_settings);
    }

    private void setDefaultTabAndIndicators() {
        Tab savedTab = getSavedTab(); // Retrieve saved tab or default to CHAT
        selectTab(savedTab);
        SharedPreferencesHelper.saveCurrentTab(HomeActivity.this, "CHAT"); // Set default tab as "CHAT"
    }

    private void setListeners() {
        taskbarFriends.setOnClickListener(v -> selectTab(Tab.FRIENDS));
        taskbarChat.setOnClickListener(v -> selectTab(Tab.CHAT));
        taskbarSettings.setOnClickListener(v -> selectTab(Tab.SETTINGS));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchFriendsActivity.class)));
        createChatIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CreateChatActivity.class)));
    }

    private enum Tab {
        FRIENDS, CHAT, SETTINGS
    }

    private void selectTab(Tab tab) {
        // Hide all indicators and icons before showing the selected tab
        hideAllIndicators();

        // Switch fragment and update UI based on selected tab
        switch (tab) {
            case FRIENDS:
                replaceFragment(new FriendsFragment());
                updateTitle("Friends");
                showIndicatorAndIcon(indicatorFriends, searchIcon);
                break;

            case CHAT:
                replaceFragment(new ChatFragment());
                updateTitle("Chat");
                showIndicatorAndIcon(indicatorChat, createChatIcon);
                break;

            case SETTINGS:
                replaceFragment(new SettingsFragment());
                updateTitle("Settings");
                indicatorSettings.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideAllIndicators() {
        indicatorFriends.setVisibility(View.INVISIBLE);
        indicatorChat.setVisibility(View.INVISIBLE);
        indicatorSettings.setVisibility(View.INVISIBLE);
        searchIcon.setVisibility(View.GONE);
        createChatIcon.setVisibility(View.GONE);
    }

    private void showIndicatorAndIcon(View indicator, View icon) {
        indicator.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void updateTitle(String title) {
        topBarTitle.setText(title);
    }

    // Retrieve saved tab from SharedPreferences (or default to CHAT if null)
    private Tab getSavedTab() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedTab = sharedPreferences.getString(KEY_SELECTED_TAB, null); // Default to null if not set
        return savedTab == null ? Tab.CHAT : Tab.valueOf(savedTab); // Return CHAT if no saved tab exists
    }
}