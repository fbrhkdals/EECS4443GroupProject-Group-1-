package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Chat.Chat;
import com.example.eecs4443groupprojectgroup1.Adapter.ChatLogAdapter;
import com.example.eecs4443groupprojectgroup1.Chat.ChatViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ImageButton backButton, sendButton;
    private int friendId;
    private ChatViewModel chatViewModel;
    private UserViewModel userViewModel;
    private EditText messageInput;
    private TextView friendName;

    // RecyclerView for chat messages
    private RecyclerView chatRecyclerView;
    private ChatLogAdapter chatLogAdapter;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // Initialize views and layout
        initializeViews();

        // Get current user ID from SharedPreferences
        int userId = SharedPreferencesHelper.getUserId(this);

        // Receive friendId from the Intent
        friendId = getIntent().getIntExtra("friendId", -1); // Default value is -1

        if (friendId == -1) {
            // If friendId is invalid, show error and navigate back
            showErrorAndNavigateHome();
            return;
        }

        // Observe the friend's name and update UI
        observeFriendName();

        // Mark messages as read
        markMessagesAsRead(userId);

        // Initialize RecyclerView and message sending functionality
        setupRecyclerView();
        setupSendButton();

        // Observe messages between the user and the friend
        observeMessages(userId);
    }

    private void initializeViews() {
        // Set up root layout to handle window insets (navigation and keyboard)
        View rootView = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(0, 0, 0, Math.max(navBarInsets.bottom, imeInsets.bottom));
            return insets;
        });

        // Initialize ViewModels
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize UI components
        backButton = findViewById(R.id.back_button);
        sendButton = findViewById(R.id.send_button);
        messageInput = findViewById(R.id.message_input);
        friendName = findViewById(R.id.friend_name);

        // Set up back button to navigate to HomeActivity
        backButton.setOnClickListener(v -> navigateToHome());
    }

    private void observeFriendName() {
        // Observe friend's name and set it to the TextView
        userViewModel.getUserById(friendId).observe(this, user -> {
            if (user != null) {
                friendName.setText(user.username);
            } else {
                friendName.setText("");
            }
        });
    }

    private void markMessagesAsRead(int userId) {
        if (userId != -1 && friendId != -1) {
            chatViewModel.markMessagesAsRead(userId, friendId);
        }
    }

    private void setupRecyclerView() {
        // Initialize RecyclerView with adapter
        chatRecyclerView = findViewById(R.id.messages_recycler_view);
        chatLogAdapter = new ChatLogAdapter(SharedPreferencesHelper.getUserId(this), userViewModel, this);
        chatRecyclerView.setAdapter(chatLogAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Register a data observer to scroll to the latest message when new messages are added
        chatLogAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (chatLogAdapter.getItemCount() > 0) {
                    chatRecyclerView.smoothScrollToPosition(chatLogAdapter.getItemCount() - 1);
                }
            }
        });
    }

    private void setupSendButton() {
        // Send button functionality
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();

            if (!message.isEmpty()) {
                int senderId = SharedPreferencesHelper.getUserId(ChatActivity.this);
                if (senderId != -1) {
                    Chat chat = new Chat(senderId, friendId, message, false, "text");
                    chatViewModel.sendMessage(chat);
                    messageInput.setText("");  // Clear input field after sending
                }
            }
        });
    }

    private void observeMessages(int userId) {
        // Observe chat messages between the current user and the friend
        if (userId != -1 && friendId != -1) {
            chatViewModel.getChatBetweenUsers(userId, friendId).observe(this, chats -> {
                chatLogAdapter.submitList(chats);

                if (chats != null && !chats.isEmpty()) {
                    chatRecyclerView.scrollToPosition(chats.size() - 1);  // Scroll to the last message
                }
            });
        }
    }

    private void showErrorAndNavigateHome() {
        // Show error message and navigate to HomeActivity
        Toast.makeText(this, "Friend not found, returning to Home.", Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    private void navigateToHome() {
        Intent homeIntent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();  // Close the current ChatActivity
    }
}