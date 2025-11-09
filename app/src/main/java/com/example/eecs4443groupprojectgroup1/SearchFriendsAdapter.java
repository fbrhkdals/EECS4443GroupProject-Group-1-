package com.example.eecs4443groupprojectgroup1;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchFriendsAdapter extends RecyclerView.Adapter<SearchFriendsAdapter.UserViewHolder> {

    private List<FriendRepository.UserWithCommonFriends> originalUsers; // Original full list
    private List<FriendRepository.UserWithCommonFriends> filteredUsers; // Filtered list for search
    private FriendViewModel friendViewModel;  // ViewModel to manage friend requests
    private FriendsDao friendDao;             // DAO to query friend request status
    private LifecycleOwner lifecycleOwner;    // Required for LiveData observation

    // Constructor
    public SearchFriendsAdapter(List<FriendRepository.UserWithCommonFriends> usersWithCommonFriends,
                                FriendViewModel friendViewModel,
                                FriendsDao friendDao,
                                LifecycleOwner lifecycleOwner) {
        this.originalUsers = new ArrayList<>(usersWithCommonFriends);
        this.filteredUsers = usersWithCommonFriends;
        this.friendViewModel = friendViewModel;
        this.friendDao = friendDao;
        this.lifecycleOwner = lifecycleOwner;
    }

    /**
     * ViewHolder class for individual user items.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public ImageView userIconImageView;
        public Button addFriendButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text);
            userIconImageView = itemView.findViewById(R.id.user_icon);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single user item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        FriendRepository.UserWithCommonFriends userWithCommonFriends = filteredUsers.get(position);

        // Set username
        holder.usernameTextView.setText(userWithCommonFriends.user.username);

        // Set user icon (decode from Base64)
        String base64Image = userWithCommonFriends.user.userIcon;
        Bitmap userIcon = ImageUtil.decodeFromBase64(base64Image);
        if (userIcon != null) {
            holder.userIconImageView.setImageBitmap(userIcon);
        } else {
            holder.userIconImageView.setImageResource(R.drawable.user_icon); // default icon
        }

        // Observe friend request status to update button text
        observeFriendRequestStatus(userWithCommonFriends, holder);

        // Handle "Add Friend" button click
        holder.addFriendButton.setOnClickListener(v -> {
            Context context = v.getContext();
            int currentUserId = SharedPreferencesHelper.getUserId(context); // Get logged-in user ID
            int friendId = userWithCommonFriends.user.id;
            String buttonText = holder.addFriendButton.getText().toString();

            if ("Send Request".equals(buttonText)) {
                // Send friend request if not already sent
                if (friendViewModel != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Friend existing = friendViewModel.getFriendRequest(currentUserId, friendId);
                        if (existing == null) {
                            friendViewModel.sendFriendRequest(currentUserId, friendId);
                        }
                        friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "pending");

                        // Show Toast on UI thread
                        holder.addFriendButton.post(() ->
                                Toast.makeText(context, "Friend request sent to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show()
                        );
                    });
                }
            } else if ("Cancel".equals(buttonText)) {
                // Cancel existing friend request
                if (friendViewModel != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "cancelled");

                        // Show Toast on UI thread
                        holder.addFriendButton.post(() ->
                                Toast.makeText(context, "Friend request canceled to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show()
                        );
                    });
                }
            }
        });

        // Handle item click -> show user popup
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            View popupView = LayoutInflater.from(context).inflate(R.layout.user_popup, null);

            // Bind views in popup
            ImageView popupUserIcon = popupView.findViewById(R.id.popup_user_icon);
            TextView popupUsername = popupView.findViewById(R.id.popup_username);
            TextView popupDescription = popupView.findViewById(R.id.popup_description);

            popupUsername.setText(userWithCommonFriends.user.username);

            // Show description or default text
            String description = userWithCommonFriends.user.description;
            if (description == null || description.isEmpty()) {
                description = "User has no Description.";
            }
            popupDescription.setText(description);

            // Set user icon in popup
            Bitmap userIconBitmap = ImageUtil.decodeFromBase64(userWithCommonFriends.user.userIcon);
            if (userIconBitmap != null) {
                popupUserIcon.setImageBitmap(userIconBitmap);
            } else {
                popupUserIcon.setImageResource(R.drawable.user_icon);
            }

            // Create AlertDialog with Close button
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setView(popupView)
                    .setPositiveButton("Close", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create();

            // Set Close button text color to black when dialog shows
            dialog.setOnShowListener(d -> {
                Button closeButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                if (closeButton != null) {
                    closeButton.setTextColor(context.getResources().getColor(android.R.color.black));
                }
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredUsers != null ? filteredUsers.size() : 0;
    }

    /**
     * Observe friend request status from database and update the button text dynamically.
     */
    private void observeFriendRequestStatus(FriendRepository.UserWithCommonFriends userWithCommonFriends, UserViewHolder holder) {
        int currentUserId = SharedPreferencesHelper.getUserId(holder.itemView.getContext());
        friendDao.getFriendRequestStatus(currentUserId, userWithCommonFriends.user.id)
                .observe(lifecycleOwner, status -> {
                    if ("pending".equals(status)) {
                        holder.addFriendButton.setText("Cancel"); // Friend request already sent
                    } else {
                        holder.addFriendButton.setText("Send Request"); // No friend request
                    }
                });
    }

    /**
     * Update the adapter with a new list of users.
     */
    public void updateUsers(List<FriendRepository.UserWithCommonFriends> newUsers) {
        this.originalUsers = new ArrayList<>(newUsers);
        this.filteredUsers = newUsers;
        notifyDataSetChanged();
    }

    /**
     * Filter users based on a search query.
     */
    public void filterUsers(String query) {
        if (query.isEmpty()) {
            filteredUsers = originalUsers;
        } else {
            List<FriendRepository.UserWithCommonFriends> filteredList = new ArrayList<>();
            for (FriendRepository.UserWithCommonFriends user : originalUsers) {
                if (user.user.username.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user);
                }
            }
            filteredUsers = filteredList;
        }
        notifyDataSetChanged();
    }
}