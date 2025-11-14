package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Activity.HomeActivity;
import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backButton;
    private EditText searchEditText;
    private CreateChatAdapter createChatAdapter; // Use CreateChatAdapter here
    private FriendViewModel friendViewModel;
    private UserViewModel userViewModel;
    private UserRepository userRepository;
    private List<Friend> acceptedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chat);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.createChat_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Use LinearLayoutManager for vertical list

        // Initialize UserRepository
        userRepository = new UserRepository(this);

        // Initialize FriendViewModel with custom factory
        AppDatabase appDatabase = AppDatabase.getInstance(this); // Get database instance
        FriendViewModel.Factory factory = new FriendViewModel.Factory(appDatabase);
        friendViewModel = new ViewModelProvider(this, factory).get(FriendViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize the RecyclerView adapter with an empty list for now
        createChatAdapter = new CreateChatAdapter( // Use CreateChatAdapter instead of FriendsAdapter
                new ArrayList<Friend>(), // initial empty list
                userRepository,
                this, // This is the LifecycleOwner (CreateChatActivity is a LifecycleOwner)
                friendViewModel,
                userViewModel
        );
        recyclerView.setAdapter(createChatAdapter); // Set adapter to RecyclerView

        // Get the current user's ID from shared preferences
        int userId = SharedPreferencesHelper.getUserId(this);

        // Observe accepted friend requests
        friendViewModel.getReceivedFriendRequestsByStatusSorted(userId, "accepted")
                .observe(this, accepted -> {
                    // Update accepted list or use empty list if null
                    acceptedList = accepted != null ? accepted : new ArrayList<>();
                    updateRecyclerView(); // Update RecyclerView with new data
                });

        // Set up the back button to return to HomeActivity
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateChatActivity.this, HomeActivity.class));
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
                createChatAdapter.filterUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    // Updates the RecyclerView with the accepted friend list.
    private void updateRecyclerView() {
        if (acceptedList != null && !acceptedList.isEmpty()) {
            // Update adapter with the accepted friends list
            createChatAdapter.updateFriends(acceptedList);
        } else {
            // If no accepted friends, show an empty state or a message
            createChatAdapter.updateFriends(new ArrayList<Friend>()); // Pass an empty list to show no data
        }
    }
}