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
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<FriendRepository.UserWithCommonFriends> originalUsers;
    private List<FriendRepository.UserWithCommonFriends> filteredUsers;
    private FriendViewModel friendViewModel;  // ViewModel to manage friend requests
    private FriendsDao friendDao; // FriendDao to query friend request status

    // Constructor to initialize list, ViewModel, and FriendDao
    public UserAdapter(List<FriendRepository.UserWithCommonFriends> usersWithCommonFriends,
                       FriendViewModel friendViewModel, FriendsDao friendDao) {
        this.originalUsers = new ArrayList<>(usersWithCommonFriends);  // Make a copy of the original list
        this.filteredUsers = usersWithCommonFriends;
        this.friendViewModel = friendViewModel;  // Pass the ViewModel
        this.friendDao = friendDao;  // Pass the FriendDao to query the database
    }

    // ViewHolder class to bind each item (user) in the list
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public ImageView userIconImageView;
        public Button addFriendButton;  // Button for friend request

        public UserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text);
            userIconImageView = itemView.findViewById(R.id.user_icon);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);  // Initialize the button
        }
    }

    // Create a new ViewHolder by inflating the item layout
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    // Bind the data (user information) to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        FriendRepository.UserWithCommonFriends userWithCommonFriends = filteredUsers.get(position);

        // Set the username text
        holder.usernameTextView.setText(userWithCommonFriends.user.username);

        // Convert the Base64 string to Bitmap and set it to ImageView
        String base64Image = userWithCommonFriends.user.userIcon;
        Bitmap userIcon = ImageUtil.decodeFromBase64(base64Image);  // Decode the Base64 image

        // If the image is valid, set it to the ImageView
        if (userIcon != null) {
            holder.userIconImageView.setImageBitmap(userIcon);
        } else {
            holder.userIconImageView.setImageResource(R.drawable.user_icon);  // Default placeholder
        }

        // Set the visibility of the "Add Friend" button based on whether the user is already a friend
        if (!isUserAlreadyFriend(userWithCommonFriends)) {
            // Observe the friend request status
            observeFriendRequestStatus(userWithCommonFriends, holder);
            holder.addFriendButton.setVisibility(View.VISIBLE);  // Show button if not a friend
        } else {
            holder.addFriendButton.setVisibility(View.GONE);  // Hide the button if already a friend
        }

        // Set the button click listener to handle sending/canceling a friend request
        holder.addFriendButton.setOnClickListener(v -> {
            // Get the current user's ID using SharedPreferencesHelper
            Context context = v.getContext();
            int currentUserId = SharedPreferencesHelper.getUserId(context);
            int friendId = userWithCommonFriends.user.id;

            // Get the current text of the button
            String buttonText = holder.addFriendButton.getText().toString();

            if ("Send Request".equals(buttonText)) {
                // Send the friend request
                if (friendViewModel != null) {
                    friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "pending");

                    // Show a Toast message indicating the friend request is sent
                    Toast.makeText(context, "Friend request sent to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show();
                }
            } else if ("Cancel".equals(buttonText)) {
                // Cancel the friend request
                if (friendViewModel != null) {
                    friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "cancelled");

                    // Show a Toast message indicating the request is canceled
                    Toast.makeText(context, "Friend request canceled to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Return the total number of items (users) in the filtered list
    @Override
    public int getItemCount() {
        return filteredUsers != null ? filteredUsers.size() : 0;
    }

    // Helper method to check if the user is already a friend
    private boolean isUserAlreadyFriend(FriendRepository.UserWithCommonFriends userWithCommonFriends) {
        return userWithCommonFriends.commonFriendsCount > 0;
    }

    // Helper method to observe the friend request status (status = "pending")
    private void observeFriendRequestStatus(FriendRepository.UserWithCommonFriends userWithCommonFriends, UserViewHolder holder) {
        // Observe the LiveData for the friend request status
        friendDao.getFriendRequestStatus(SharedPreferencesHelper.getUserId(holder.itemView.getContext()), userWithCommonFriends.user.id)
                .observe((LifecycleOwner) holder.itemView.getContext(), new Observer<String>() {
                    @Override
                    public void onChanged(String status) {
                        // Update button text based on the status
                        if ("pending".equals(status)) {
                            holder.addFriendButton.setText("Cancel");
                            holder.addFriendButton.setEnabled(true);
                        } else {
                            holder.addFriendButton.setText("Send Request");
                            holder.addFriendButton.setEnabled(true);
                        }
                    }
                });
    }

    // Method to update the list of users and notify the adapter to refresh the UI
    public void updateUsers(List<FriendRepository.UserWithCommonFriends> newUsers) {
        this.originalUsers = new ArrayList<>(newUsers);
        this.filteredUsers = newUsers;
        notifyDataSetChanged();
    }

    // Method to filter the users based on the search query
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
        notifyDataSetChanged();  // Notify the adapter to update the UI
    }
}