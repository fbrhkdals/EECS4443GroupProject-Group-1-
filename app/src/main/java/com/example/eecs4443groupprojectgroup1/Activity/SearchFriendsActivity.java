package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Friend.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.Friend.FriendsDao;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Adapter.SearchFriendsAdapter;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.Collections;

public class SearchFriendsActivity extends AppCompatActivity {

    private ImageButton backButton; // Button to go back to the previous screen
    private RecyclerView recyclerView; // RecyclerView to display the list of friends
    private SearchFriendsAdapter userAdapter; // Adapter for RecyclerView
    private FriendViewModel friendViewModel; // ViewModel to interact with friend data
    private EditText searchEditText; // EditText for searching friends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriends); // Set the layout for this activity

        // Initialize the FriendViewModel which will handle data fetching and business logic
        friendViewModel = new ViewModelProvider(this, new FriendViewModel.Factory(AppDatabase.getInstance(this)))
                .get(FriendViewModel.class);

        // Initialize RecyclerView and set its LayoutManager to display items in a vertical list
        recyclerView = findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve FriendDao from the AppDatabase instance to perform database operations
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        FriendsDao friendDao = appDatabase.friendDao();  // Get FriendDao instance for DB operations

        // Initialize the adapter with an empty list initially, and pass the necessary objects (ViewModel, DAO)
        userAdapter = new SearchFriendsAdapter(Collections.emptyList(), friendViewModel, friendDao, this);
        recyclerView.setAdapter(userAdapter); // Set the adapter to the RecyclerView

        // Get the current user's ID from SharedPreferences to know which user's friends to display
        int userId = SharedPreferencesHelper.getUserId(SearchFriendsActivity.this);

        // Observe changes in the sorted list of friends (sorted by common friends)
        friendViewModel.getSortedFriendsByCommonFriends(userId).observe(this, usersWithCommonFriends -> {
            // When data changes (e.g., new friends or updates), update the adapter's list of users
            userAdapter.updateUsers(usersWithCommonFriends);
        });

        // Set up the back button click listener to navigate back to HomeActivity
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            SharedPreferencesHelper.saveCurrentTab(SearchFriendsActivity.this, "FRIENDS"); // Save the current tab state
            startActivity(new Intent(SearchFriendsActivity.this, HomeActivity.class)); // Navigate to HomeActivity
            finish(); // Finish this activity to remove it from the stack
        });

        // Initialize the search EditText for filtering friends by name or other attributes
        searchEditText = findViewById(R.id.search_edittext);

        // Add a TextWatcher to listen for text changes in the search field
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // As the text changes, filter the displayed users in the adapter
                userAdapter.filterUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }
}