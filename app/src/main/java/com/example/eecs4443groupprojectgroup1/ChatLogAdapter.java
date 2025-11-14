package com.example.eecs4443groupprojectgroup1;

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

import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.Objects;

public class ChatLogAdapter extends ListAdapter<Chat, ChatLogAdapter.ChatViewHolder> {

    private int currentUserId; // ID of logged-in
    private UserViewModel userViewModel;
    private LifecycleOwner lifecycleOwner;


    // Constructor to pass current user ID
    public ChatLogAdapter(int currentUserId, UserViewModel userViewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        this.userViewModel = userViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_log, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = getItem(position);
        boolean isSentByCurrentUser = chat.senderId == currentUserId;

        if (isSentByCurrentUser) {
            holder.userText.setVisibility(View.VISIBLE);
            holder.userText.setText(chat.message);

            holder.friendText.setVisibility(View.GONE);
            holder.friendIcon.setVisibility(View.GONE);
        } else {
            holder.friendText.setVisibility(View.VISIBLE);
            holder.friendText.setText(chat.message);

            // Load friend's icon from repository
            userViewModel.getUserById(chat.senderId).observe(lifecycleOwner, user -> {
                if (user != null) {
                    Bitmap iconBitmap = ImageUtil.decodeFromBase64(user.userIcon);
                    if (iconBitmap != null) holder.friendIcon.setImageBitmap(iconBitmap);
                    else holder.friendIcon.setImageResource(R.drawable.user_icon); // default icon
                }
            });

            // Check the previous message
            if (position == 0 || getItem(position - 1).senderId != chat.senderId) {
                holder.friendIcon.setVisibility(View.VISIBLE); // First message or after a different sender
            } else {
                holder.friendIcon.setVisibility(View.INVISIBLE); // Hide icon for consecutive messages from the same sender
            }

            holder.userText.setVisibility(View.GONE);
        }
    }

    // ViewHolder for chat item
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView friendIcon;
        TextView friendText, userText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            friendIcon = itemView.findViewById(R.id.friend_icon);
            friendText = itemView.findViewById(R.id.friend_text);
            userText = itemView.findViewById(R.id.user_text);
        }
    }

    // DiffUtil for efficiently updating RecyclerView
    private static final DiffUtil.ItemCallback<Chat> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Chat>() {
                @Override
                public boolean areItemsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return oldItem.messageId == newItem.messageId;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Chat oldItem, @NonNull Chat newItem) {
                    return Objects.equals(oldItem.message, newItem.message)
                            && oldItem.isRead == newItem.isRead
                            && oldItem.timestamp == newItem.timestamp;
                }
            };
}