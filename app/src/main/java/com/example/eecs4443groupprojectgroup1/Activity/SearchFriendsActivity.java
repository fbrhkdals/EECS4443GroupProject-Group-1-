package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.UserAdapter;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

public class SearchFriendsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private FriendViewModel friendViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriends);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter with an empty list initially
        userAdapter = new UserAdapter(null);
        recyclerView.setAdapter(userAdapter);

        // Initialize the ViewModel
        friendViewModel = new ViewModelProvider(this, new FriendViewModel.Factory(AppDatabase.getInstance(this)))
                .get(FriendViewModel.class);

        // Get the current user's ID from SharedPreferences
        int userId = SharedPreferencesHelper.getUserId(SearchFriendsActivity.this);

        // Observe the sorted friends data
        friendViewModel.getSortedFriendsByCommonFriends(userId).observe(this, usersWithCommonFriends -> {
            // Update RecyclerView when data changes
            userAdapter.updateUsers(usersWithCommonFriends);
        });

        // Set up the back button to return to HomeActivity
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            SharedPreferencesHelper.saveCurrentTab(SearchFriendsActivity.this, "FRIENDS");
            startActivity(new Intent(SearchFriendsActivity.this, HomeActivity.class));
            finish();
        });
    }
}
