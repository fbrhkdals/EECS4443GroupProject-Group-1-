package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.FriendsDao;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.SearchFriendsAdapter;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.Collections;

public class SearchFriendsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView recyclerView;
    private SearchFriendsAdapter userAdapter;
    private FriendViewModel friendViewModel;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriends);

        // Initialize the ViewModel first
        friendViewModel = new ViewModelProvider(this, new FriendViewModel.Factory(AppDatabase.getInstance(this)))
                .get(FriendViewModel.class);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.users_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve FriendDao from the AppDatabase instance
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        FriendsDao friendDao = appDatabase.friendDao();  // Get FriendDao instance

        // Set up the adapter after the ViewModel is initialized
        userAdapter = new SearchFriendsAdapter(Collections.emptyList(), friendViewModel, friendDao, this);  // Pass friendDao here
        recyclerView.setAdapter(userAdapter);

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

        // Initialize the search EditText
        searchEditText = findViewById(R.id.search_edittext);

        // Add TextWatcher to listen for changes in the search input
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Filter the user list as the search text changes
                userAdapter.filterUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }
}