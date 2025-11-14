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

import com.example.eecs4443groupprojectgroup1.CreateChatActivity;
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

        // Find views by their IDs
        taskbarFriends = findViewById(R.id.taskbar_friends);
        taskbarChat = findViewById(R.id.taskbar_chat);
        taskbarSettings = findViewById(R.id.taskbar_settings);
        topBarTitle = findViewById(R.id.top_bar_title);
        searchIcon = findViewById(R.id.search_icon);
        createChatIcon = findViewById(R.id.CreateChat_icon);


        // Find indicator views (must be declared in XML layout)
        indicatorFriends = findViewById(R.id.indicator_friends);
        indicatorChat = findViewById(R.id.indicator_chat);
        indicatorSettings = findViewById(R.id.indicator_settings);

        // Set default fragment and indicator based on shared preferences
        Tab savedTab = getSavedTab();
        selectTab(savedTab);
        SharedPreferencesHelper.saveCurrentTab(HomeActivity.this, "CHAT");

        // Set click listeners
        taskbarFriends.setOnClickListener(v -> selectTab(Tab.FRIENDS));
        taskbarChat.setOnClickListener(v -> selectTab(Tab.CHAT));
        taskbarSettings.setOnClickListener(v -> selectTab(Tab.SETTINGS));
        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchFriendsActivity.class);
            startActivity(intent);
        });
        createChatIcon.setOnClickListener( v -> {
            Intent intent = new Intent(HomeActivity.this, CreateChatActivity.class);
            startActivity(intent);
        });
    }

    private enum Tab {
        FRIENDS, CHAT, SETTINGS
    }

    private void selectTab(Tab tab) {
        // Hide all indicators first
        indicatorFriends.setVisibility(View.INVISIBLE);
        indicatorChat.setVisibility(View.INVISIBLE);
        indicatorSettings.setVisibility(View.INVISIBLE);
        searchIcon.setVisibility(View.GONE);
        createChatIcon.setVisibility(View.GONE);

        // Show selected indicator and update UI
        switch (tab) {
            case FRIENDS:
                replaceFragment(new FriendsFragment());
                updateTitle("Friends");
                indicatorFriends.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);
                break;

            case CHAT:
                replaceFragment(new ChatFragment());
                updateTitle("Chat");
                indicatorChat.setVisibility(View.VISIBLE);
                createChatIcon.setVisibility(View.VISIBLE);
                break;

            case SETTINGS:
                replaceFragment(new SettingsFragment());
                updateTitle("Settings");
                indicatorSettings.setVisibility(View.VISIBLE);
                break;
        }
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
        String savedTab = sharedPreferences.getString(KEY_SELECTED_TAB, null); // Read saved tab, default to null
        if (savedTab == null) {
            return Tab.CHAT; // If null, return CHAT as default tab
        } else {
            return Tab.valueOf(savedTab); // Convert string back to enum
        }
    }
}