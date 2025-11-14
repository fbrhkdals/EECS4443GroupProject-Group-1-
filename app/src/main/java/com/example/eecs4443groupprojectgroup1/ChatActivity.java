package com.example.eecs4443groupprojectgroup1;

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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Activity.HomeActivity;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        View rootView = findViewById(R.id.root_layout);

        // Apply bottom padding only for navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    0, // left
                    0, // top
                    0, // right
                    Math.max(navBarInsets.bottom, imeInsets.bottom)
            );
            return insets;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize RecyclerView
        chatRecyclerView = findViewById(R.id.messages_recycler_view); // Make sure you added RecyclerView in layout
        chatLogAdapter = new ChatLogAdapter(
                SharedPreferencesHelper.getUserId(this),
                userViewModel,
                this
        ); // Pass current user ID
        chatRecyclerView.setAdapter(chatLogAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        friendName = findViewById(R.id.friend_name);

        int userId = SharedPreferencesHelper.getUserId(this);

        // Receive friendId from the Intent
        Intent intent = getIntent();
        friendId = intent.getIntExtra("friendId", -1);  // Default value is set to -1

        // Check if the friend ID was properly passed
        if (friendId != -1) {

            // Observe the friend's name using LiveData and update the UI when data changes
            userViewModel.getUserById(friendId).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        // If the user is not null, set the friend's name in the TextView
                        friendName.setText(user.username);
                    } else {
                        // If the friend data is not found, set the TextView to an empty string
                        friendName.setText("");
                    }
                }
            });
        } else {
            // If friend ID is not received, navigate back to HomeActivity
            Toast.makeText(this, "Friend not found, returning to Home.", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();  // Close ChatActivity
        }

        // Initialize ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Mark Messages as Read for user
        if (userId != -1 && friendId != -1) {
            chatViewModel.markMessagesAsRead(userId, friendId);
        }

        // Send button click handler
        sendButton.setOnClickListener(v -> {
            // Get the message from EditText
            String message = messageInput.getText().toString().trim();  // Trim leading/trailing spaces

            // Check if the message is not empty
            if (!message.isEmpty()) {
                // Get user ID from SharedPreferences
                int senderId = SharedPreferencesHelper.getUserId(ChatActivity.this);

                if (senderId != -1) {
                    // Create Chat object and send it via ViewModel
                    Chat chat = new Chat(
                            senderId,  // Get the logged-in user ID from SharedPreferences
                            friendId,
                            message,
                            false, // Assuming the message is unread initially
                            "text" // Message type "text"
                    );

                    // Send message via ViewModel
                    chatViewModel.sendMessage(chat);

                    // Clear the input field
                    messageInput.setText("");
                }
            }
        });

        // Observe messages between user and friend
        if (userId != -1 && friendId != -1) {
            chatViewModel.getChatBetweenUsers(userId, friendId).observe(this, new Observer<List<Chat>>() {
                @Override
                public void onChanged(List<Chat> chats) {
                    // Update RecyclerView with the latest messages
                    chatLogAdapter.submitList(chats);

                    // Scroll to the last message
                    chatRecyclerView.scrollToPosition(chats.size() - 1);
                }
            });
        }

        // Register an AdapterDataObserver to scroll to the bottom when a new message is inserted
        chatLogAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                // Smoothly scroll to the last message
                chatRecyclerView.smoothScrollToPosition(chatLogAdapter.getItemCount() - 1);
            }
        });

        // Initialize the back button using the inflated view
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Start HomeActivity when the back button is clicked
            Intent homeIntent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }
}