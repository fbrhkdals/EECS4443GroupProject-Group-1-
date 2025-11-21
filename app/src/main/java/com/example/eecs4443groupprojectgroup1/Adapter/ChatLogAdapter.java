package com.example.eecs4443groupprojectgroup1.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Chat.Chat;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.Objects;

public class ChatLogAdapter extends ListAdapter<Chat, ChatLogAdapter.ChatViewHolder> {

    private int currentUserId; // ID of the logged-in user
    private UserViewModel userViewModel; // ViewModel to get user information
    private LifecycleOwner lifecycleOwner; // LifecycleOwner for observing LiveData

    // Constructor to pass current user ID, UserViewModel, and LifecycleOwner
    public ChatLogAdapter(int currentUserId, UserViewModel userViewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat item layout and create ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_log, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Get the chat item at the current position
        Chat chat = getItem(position);
        boolean isSentByCurrentUser = chat.senderId == currentUserId;

        if (isSentByCurrentUser) {
            // If the message is sent by the current user
            holder.userText.setVisibility(View.VISIBLE);
            holder.userText.setText(chat.message); // Set user's message

            // Hide friend's message and icon
            holder.friendText.setVisibility(View.GONE);
            holder.friendIcon.setVisibility(View.GONE);
        } else {
            // If the message is from the friend
            holder.friendText.setVisibility(View.VISIBLE);
            holder.friendText.setText(chat.message); // Set friend's message

            // Load friend's icon from the userViewModel
            userViewModel.getUserById(chat.senderId).observe(lifecycleOwner, user -> {
                if (user != null) {
                    // Decode and set the friend's icon
                    Bitmap iconBitmap = ImageUtil.decodeFromBase64(user.userIcon);
                    if (iconBitmap != null) {
                        holder.friendIcon.setImageBitmap(iconBitmap);
                    } else {
                        holder.friendIcon.setImageResource(R.drawable.user_icon); // Default icon
                    }
                }
            });

            // Check if the previous message was sent by the same user
            if (position == 0 || getItem(position - 1).senderId != chat.senderId) {
                holder.friendIcon.setVisibility(View.VISIBLE); // Show icon if it's the first message or different sender
            } else {
                holder.friendIcon.setVisibility(View.INVISIBLE); // Hide icon for consecutive messages from the same sender
            }

            holder.userText.setVisibility(View.GONE); // Hide user's message
        }
    }

    // ViewHolder class to hold references to the views
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView friendIcon; // Icon of the friend
        TextView friendText, userText; // Messages from friend and user

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            friendIcon = itemView.findViewById(R.id.friend_icon);
            friendText = itemView.findViewById(R.id.friend_text);
            userText = itemView.findViewById(R.id.user_text);
        }
    }

    // DiffUtil callback for efficient updates of RecyclerView
    private static final DiffUtil.ItemCallback<Chat> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Chat>() {

                @Override
                public boolean areItemsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    // Compare messages by their unique message ID
                    return oldItem.messageId == newItem.messageId;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    // Compare the content of the messages (message text, read status, and timestamp)
                    return Objects.equals(oldItem.message, newItem.message)
                            && oldItem.isRead == newItem.isRead
                            && oldItem.timestamp == newItem.timestamp;
                }
            };
}