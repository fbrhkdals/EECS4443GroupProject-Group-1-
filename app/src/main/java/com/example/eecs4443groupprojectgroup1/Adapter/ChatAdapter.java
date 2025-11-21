package com.example.eecs4443groupprojectgroup1.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Chat.Chat;
import com.example.eecs4443groupprojectgroup1.Chat.ChatViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(int friendId);  // Item click callback with friendId
    }

    private OnItemClickListener listener;

    // Constructor to initialize the adapter
    public ChatAdapter(Context context, List<Chat> chatList, UserViewModel userViewModel, ChatViewModel chatViewModel, OnItemClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.userViewModel = userViewModel;
        this.chatViewModel = chatViewModel;
        this.listener = listener;
    }

    // Update the chat list efficiently
    public void setChatList(List<Chat> newChatList) {
        if (newChatList != null && !newChatList.equals(chatList)) {
            chatList = newChatList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat item layout and create the ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Bind the chat data to the ViewHolder
        Chat chat = chatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the chat list
        return chatList == null ? 0 : chatList.size();
    }

    // ViewHolder class to bind the chat item views
    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText, recentText;
        ImageView userIcon;
        TextView unreadCountText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            usernameText = itemView.findViewById(R.id.username_text);
            recentText = itemView.findViewById(R.id.recent_text);
            userIcon = itemView.findViewById(R.id.user_icon);
            unreadCountText = itemView.findViewById(R.id.unread_count_text);

            // Set click listener for the itemView to notify the listener with the friendId
            itemView.setOnClickListener(v -> {
                int friendUserId = getFriendUserId(getAdapterPosition());
                if (listener != null) {
                    listener.onItemClick(friendUserId); // Notify the listener with the friendId
                }
            });
        }

        // Bind chat data to the views
        void bind(Chat chat) {
            int currentUserId = SharedPreferencesHelper.getUserId(context);
            // Determine the friend ID based on the chat sender/receiver
            int friendUserId = (chat.senderId == currentUserId) ? chat.receiverId : chat.senderId;

            // Observe user data for the friend using LiveData
            userViewModel.getUserById(friendUserId).observe((LifecycleOwner) itemView.getContext(), friendUser -> {
                if (friendUser != null) {
                    usernameText.setText(friendUser.username); // Set username
                    // Decode the user icon from Base64
                    Bitmap iconBitmap = ImageUtil.decodeFromBase64(friendUser.userIcon);
                    if (iconBitmap != null) {
                        userIcon.setImageBitmap(iconBitmap); // Set user icon if available
                    } else {
                        userIcon.setImageResource(R.drawable.user_icon); // Default icon if no icon is available
                    }
                    userIcon.setVisibility(View.VISIBLE);  // Make sure the icon is visible
                    recentText.setText(chat.message); // Set recent chat message

                    // Check message length to apply ellipsis if necessary
                    int maxLength = 90; // Maximum length for 2 lines
                    if (chat.message.length() > maxLength) {
                        recentText.setEllipsize(TextUtils.TruncateAt.END);
                        recentText.setMaxLines(2);
                    } else {
                        recentText.setEllipsize(null);
                        recentText.setMaxLines(Integer.MAX_VALUE); // Allow for longer messages
                    }
                } else {
                    // Handle case where user data is not found
                    usernameText.setText("Unknown User");
                    userIcon.setImageResource(R.drawable.user_icon); // Default icon
                    userIcon.setVisibility(View.VISIBLE);
                }
            });

            // Observe unread message count
            chatViewModel.getUnreadMessageCount(currentUserId, friendUserId).observe((LifecycleOwner) itemView.getContext(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer unreadCount) {
                    // Show unread message count if greater than 0
                    if (unreadCount != null && unreadCount > 0) {
                        unreadCountText.setVisibility(View.VISIBLE);
                        unreadCountText.setText(unreadCount >= 10 ? "10+" : String.valueOf(unreadCount));
                    } else {
                        unreadCountText.setVisibility(View.GONE); // Hide if no unread messages
                    }
                }
            });
        }

        // Helper method to get friend ID for the current chat
        private int getFriendUserId(int position) {
            Chat chat = chatList.get(position);
            int currentUserId = SharedPreferencesHelper.getUserId(context);
            // Return the opposite user (sender or receiver)
            return (chat.senderId == currentUserId) ? chat.receiverId : chat.senderId;
        }
    }
}