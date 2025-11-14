package com.example.eecs4443groupprojectgroup1.Adapter;

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

import com.example.eecs4443groupprojectgroup1.Friend.Friend;
import com.example.eecs4443groupprojectgroup1.Friend.FriendRepository;
import com.example.eecs4443groupprojectgroup1.Friend.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.Friend.FriendsDao;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchFriendsAdapter extends RecyclerView.Adapter<SearchFriendsAdapter.UserViewHolder> {

    private List<FriendRepository.UserWithCommonFriends> originalUsers; // Original full list of users
    private List<FriendRepository.UserWithCommonFriends> filteredUsers; // Filtered list for search functionality
    private FriendViewModel friendViewModel;  // ViewModel used to send/track friend requests
    private FriendsDao friendDao;             // DAO for querying the friend request status
    private LifecycleOwner lifecycleOwner;    // Required for LiveData observation

    // Constructor to initialize adapter with necessary data and ViewModel/DAO
    public SearchFriendsAdapter(List<FriendRepository.UserWithCommonFriends> usersWithCommonFriends,
                                FriendViewModel friendViewModel,
                                FriendsDao friendDao,
                                LifecycleOwner lifecycleOwner) {
        this.originalUsers = new ArrayList<>(usersWithCommonFriends); // Create a copy of the full list
        this.filteredUsers = usersWithCommonFriends; // Start with the full list of users
        this.friendViewModel = friendViewModel;
        this.friendDao = friendDao;
        this.lifecycleOwner = lifecycleOwner;
    }

    /**
     * ViewHolder class for each user item in the RecyclerView.
     * This holds references to UI elements like the username, user icon, and add friend button.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView; // Displays the username of the user
        public ImageView userIconImageView; // Displays the user's profile picture
        public Button addFriendButton; // Button to send or cancel a friend request

        public UserViewHolder(View itemView) {
            super(itemView);
            // Find and bind the UI elements to the ViewHolder
            usernameTextView = itemView.findViewById(R.id.username_text);
            userIconImageView = itemView.findViewById(R.id.user_icon);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }

    // Creates the ViewHolder for each user item.
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single user item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view); // Return the ViewHolder instance
    }

    // Binds the data for each user item to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        FriendRepository.UserWithCommonFriends userWithCommonFriends = filteredUsers.get(position);

        // Set the username in the ViewHolder
        holder.usernameTextView.setText(userWithCommonFriends.user.username);

        // Decode the user's profile icon from Base64 and set it to the ImageView
        String base64Image = userWithCommonFriends.user.userIcon;
        Bitmap userIcon = ImageUtil.decodeFromBase64(base64Image);
        if (userIcon != null) {
            holder.userIconImageView.setImageBitmap(userIcon); // Set the user icon if decoded successfully
        } else {
            holder.userIconImageView.setImageResource(R.drawable.user_icon); // Default icon if decoding fails
        }

        // Observe the friend request status to dynamically update the "Add Friend" button text
        observeFriendRequestStatus(userWithCommonFriends, holder);

        // Handle "Add Friend" button click
        holder.addFriendButton.setOnClickListener(v -> {
            Context context = v.getContext();
            int currentUserId = SharedPreferencesHelper.getUserId(context); // Get the logged-in user's ID
            int friendId = userWithCommonFriends.user.id; // Get the friend's ID
            String buttonText = holder.addFriendButton.getText().toString(); // Get current button text

            if ("Send Request".equals(buttonText)) {
                // If button text is "Send Request", send a friend request
                if (friendViewModel != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        Friend existing = friendViewModel.getFriendRequest(currentUserId, friendId);
                        if (existing == null) {
                            // Send the friend request if not already sent
                            friendViewModel.sendFriendRequest(currentUserId, friendId);
                        }
                        // Update the status to "pending"
                        friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "pending");

                        // Show Toast message on the main UI thread
                        holder.addFriendButton.post(() ->
                                Toast.makeText(context, "Friend request sent to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show()
                        );
                    });
                }
            } else if ("Cancel".equals(buttonText)) {
                // If button text is "Cancel", cancel the existing friend request
                if (friendViewModel != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        friendViewModel.updateFriendRequestStatus(currentUserId, friendId, "cancelled");

                        // Show Toast message on the main UI thread
                        holder.addFriendButton.post(() ->
                                Toast.makeText(context, "Friend request canceled to " + userWithCommonFriends.user.username, Toast.LENGTH_SHORT).show()
                        );
                    });
                }
            }
        });

        // Handle item click -> show a popup with detailed user info
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            // Inflate the layout for the user detail popup
            View popupView = LayoutInflater.from(context).inflate(R.layout.user_popup, null);

            // Bind views inside the popup
            ImageView popupUserIcon = popupView.findViewById(R.id.popup_user_icon);
            TextView popupUsername = popupView.findViewById(R.id.popup_username);
            TextView popupDescription = popupView.findViewById(R.id.popup_description);

            popupUsername.setText(userWithCommonFriends.user.username); // Set username in popup

            // Show description or default message if description is empty
            String description = userWithCommonFriends.user.description;
            if (description == null || description.isEmpty()) {
                description = "User has no Description.";
            }
            popupDescription.setText(description);

            // Set the user icon in the popup
            Bitmap userIconBitmap = ImageUtil.decodeFromBase64(userWithCommonFriends.user.userIcon);
            if (userIconBitmap != null) {
                popupUserIcon.setImageBitmap(userIconBitmap); // Set user icon if decoded successfully
            } else {
                popupUserIcon.setImageResource(R.drawable.user_icon); // Default icon if decoding fails
            }

            // Create and show the dialog with a "Close" button
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

            dialog.show(); // Show the popup dialog
        });
    }

    // Return the total number of items (users) in the filtered list
    @Override
    public int getItemCount() {
        return filteredUsers != null ? filteredUsers.size() : 0;
    }

    /**
     * Observe the status of the friend request from the database
     * and dynamically update the button text (e.g., "Send Request" or "Cancel").
     */
    private void observeFriendRequestStatus(FriendRepository.UserWithCommonFriends userWithCommonFriends, UserViewHolder holder) {
        int currentUserId = SharedPreferencesHelper.getUserId(holder.itemView.getContext());
        friendDao.getFriendRequestStatus(currentUserId, userWithCommonFriends.user.id)
                .observe(lifecycleOwner, status -> {
                    if ("pending".equals(status)) {
                        holder.addFriendButton.setText("Cancel"); // Friend request is pending
                    } else {
                        holder.addFriendButton.setText("Send Request"); // No pending request
                    }
                });
    }

     // Update the adapter with a new list of users (e.g., after a search).
    public void updateUsers(List<FriendRepository.UserWithCommonFriends> newUsers) {
        this.originalUsers = new ArrayList<>(newUsers); // Replace the original list
        this.filteredUsers = newUsers; // Update the filtered list as well
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

     // Filter the users based on a search query (by username).
    public void filterUsers(String query) {
        if (query.isEmpty()) {
            filteredUsers = originalUsers; // If query is empty, show all users
        } else {
            List<FriendRepository.UserWithCommonFriends> filteredList = new ArrayList<>();
            // Filter users by matching username with the query
            for (FriendRepository.UserWithCommonFriends user : originalUsers) {
                if (user.user.username.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(user); // Add matching user to filtered list
                }
            }
            filteredUsers = filteredList; // Update the filtered list
        }
        notifyDataSetChanged(); // Refresh the RecyclerView
    }
}