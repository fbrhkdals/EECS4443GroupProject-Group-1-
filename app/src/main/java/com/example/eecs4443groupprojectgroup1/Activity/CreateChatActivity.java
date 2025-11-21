package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Adapter.CreateChatAdapter;
import com.example.eecs4443groupprojectgroup1.Friend.Friend;
import com.example.eecs4443groupprojectgroup1.Friend.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreateChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backButton;
    private EditText searchEditText;
    private CreateChatAdapter createChatAdapter;
    private FriendViewModel friendViewModel;
    private UserViewModel userViewModel;
    private List<Friend> acceptedList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chat);

        // Initialize views and view models
        initializeViews();
        initializeViewModels();

        // Set up RecyclerView and its adapter
        setupRecyclerView();

        // Get the current user's ID and observe accepted friends
        int userId = SharedPreferencesHelper.getUserId(this);
        observeAcceptedFriends(userId);

        // Set up back button listener
        setupBackButton();

        // Set up search functionality
        setupSearchFunctionality();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.createChat_recyclerview);
        backButton = findViewById(R.id.back_button);
        searchEditText = findViewById(R.id.search_edittext);
    }

    private void initializeViewModels() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        AppDatabase appDatabase = AppDatabase.getInstance(this);
        FriendViewModel.Factory factory = new FriendViewModel.Factory(appDatabase);
        friendViewModel = new ViewModelProvider(this, factory).get(FriendViewModel.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        createChatAdapter = new CreateChatAdapter(new ArrayList<>(), new UserRepository(this), this, friendViewModel, userViewModel);
        recyclerView.setAdapter(createChatAdapter);
    }

    private void observeAcceptedFriends(int userId) {
        friendViewModel.getReceivedFriendRequestsByStatusSorted(userId, "accepted")
                .observe(this, accepted -> {
                    acceptedList = accepted != null ? accepted : new ArrayList<>();
                    updateRecyclerView();
                });
    }

    private void updateRecyclerView() {
        if (acceptedList != null && !acceptedList.isEmpty()) {
            createChatAdapter.updateFriends(acceptedList);
        } else {
            createChatAdapter.updateFriends(new ArrayList<>()); // Show empty list if no friends
        }
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateChatActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Filter friends as the user types in the search bar
                createChatAdapter.filterUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}