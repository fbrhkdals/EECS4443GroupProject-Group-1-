package com.example.eecs4443groupprojectgroup1;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout taskbarFriends, taskbarChat, taskbarSettings;
    private View indicatorFriends, indicatorChat, indicatorSettings;
    private TextView topBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Find views by their IDs
        taskbarFriends = findViewById(R.id.taskbar_friends);
        taskbarChat = findViewById(R.id.taskbar_chat);
        taskbarSettings = findViewById(R.id.taskbar_settings);
        topBarTitle = findViewById(R.id.top_bar_title);

        // Find indicator views (must be declared in XML layout)
        indicatorFriends = findViewById(R.id.indicator_friends);
        indicatorChat = findViewById(R.id.indicator_chat);
        indicatorSettings = findViewById(R.id.indicator_settings);

        // Set default fragment and indicator
        selectTab(Tab.CHAT);

        // Set click listeners
        taskbarFriends.setOnClickListener(v -> selectTab(Tab.FRIENDS));
        taskbarChat.setOnClickListener(v -> selectTab(Tab.CHAT));
        taskbarSettings.setOnClickListener(v -> selectTab(Tab.SETTINGS));
    }

    private enum Tab {
        FRIENDS, CHAT, SETTINGS
    }

    private void selectTab(Tab tab) {
        // Hide all indicators first
        indicatorFriends.setVisibility(View.INVISIBLE);
        indicatorChat.setVisibility(View.INVISIBLE);
        indicatorSettings.setVisibility(View.INVISIBLE);

        // Show selected indicator and update UI
        switch (tab) {
            case FRIENDS:
                replaceFragment(new FriendsFragment());
                updateTitle("Friends");
                indicatorFriends.setVisibility(View.VISIBLE);
                break;

            case CHAT:
                replaceFragment(new ChatFragment());
                updateTitle("Chat");
                indicatorChat.setVisibility(View.VISIBLE);
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
}