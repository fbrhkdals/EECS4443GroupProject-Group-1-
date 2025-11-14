package com.example.eecs4443groupprojectgroup1;

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

import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;

    // Define an interface for item clicks
    public interface OnItemClickListener {
        void onItemClick(int friendId);
    }

    private OnItemClickListener listener;

    public ChatAdapter(Context context, List<Chat> chatList, UserViewModel userViewModel, ChatViewModel chatViewModel, OnItemClickListener listener) {
        this.context = context;
        this.chatList = chatList;
        this.userViewModel = userViewModel;
        this.chatViewModel = chatViewModel;
        this.listener = listener;
    }

    // Efficiently update the chat list
    public void setChatList(List<Chat> newChatList) {
        if (newChatList != null && !newChatList.equals(chatList)) {
            chatList = newChatList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chatList == null ? 0 : chatList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText, recentText;
        ImageView userIcon;
        TextView unreadCountText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            recentText = itemView.findViewById(R.id.recent_text);
            userIcon = itemView.findViewById(R.id.user_icon);
            unreadCountText = itemView.findViewById(R.id.unread_count_text);

            // Set the click listener for the itemView
            itemView.setOnClickListener(v -> {
                int friendUserId = getFriendUserId(getAdapterPosition()); // Get the friendId for the clicked item
                if (listener != null) {
                    listener.onItemClick(friendUserId); // Notify the listener
                }
            });
        }

        void bind(Chat chat) {
            int currentUserId = SharedPreferencesHelper.getUserId(context);
            int friendUserId = (chat.senderId == currentUserId) ? chat.receiverId : chat.senderId;

            // Observe LiveData to get user info
            userViewModel.getUserById(friendUserId).observe((LifecycleOwner) itemView.getContext(), friendUser -> {
                if (friendUser != null) {
                    usernameText.setText(friendUser.username);
                    Bitmap iconBitmap = ImageUtil.decodeFromBase64(friendUser.userIcon);
                    if (iconBitmap != null) {
                        userIcon.setImageBitmap(iconBitmap);
                    } else {
                        userIcon.setImageResource(R.drawable.user_icon); // Default icon
                    }
                    userIcon.setVisibility(View.VISIBLE);
                    recentText.setText(chat.message);

                    // Check message length for ellipsis
                    int maxLength = 90; // Limit for 2 lines
                    if (chat.message.length() > maxLength) {
                        recentText.setEllipsize(TextUtils.TruncateAt.END);
                        recentText.setMaxLines(2);
                    } else {
                        recentText.setEllipsize(null);
                        recentText.setMaxLines(Integer.MAX_VALUE);
                    }
                } else {
                    usernameText.setText("Unknown User");
                    userIcon.setImageResource(R.drawable.user_icon); // Default icon
                    userIcon.setVisibility(View.VISIBLE);
                }
            });

            // Fetch unread message count
            chatViewModel.getUnreadMessageCount(currentUserId, friendUserId).observe((LifecycleOwner) itemView.getContext(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer unreadCount) {
                    if (unreadCount != null && unreadCount > 0) {
                        unreadCountText.setVisibility(View.VISIBLE);
                        if (unreadCount >= 10) {
                            unreadCountText.setText("10+");
                        } else {
                            unreadCountText.setText(String.valueOf(unreadCount));
                        }
                    } else {
                        unreadCountText.setVisibility(View.GONE);
                    }
                }
            });
        }

        // Helper method to get friendId
        private int getFriendUserId(int position) {
            Chat chat = chatList.get(position);
            int currentUserId = SharedPreferencesHelper.getUserId(context);
            return (chat.senderId == currentUserId) ? chat.receiverId : chat.senderId;
        }
    }
}