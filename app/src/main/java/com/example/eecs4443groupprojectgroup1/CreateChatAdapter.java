package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class CreateChatAdapter extends RecyclerView.Adapter<CreateChatAdapter.FriendViewHolder> {

    private List<Friend> friends; // List of friends to display in the RecyclerView
    private List<Friend> fullFriendsList; // Full list of friends (unfiltered)
    private UserRepository userRepository; // Repository for fetching user data
    private FriendViewModel friendViewModel; // ViewModel for managing friend-related data
    private UserViewModel userViewModel; // UserViewModel to manage user data
    private LifecycleOwner lifecycleOwner; // LifecycleOwner to observe LiveData and manage lifecycle

    // Constructor to initialize the adapter with necessary data sources
    public CreateChatAdapter(List<Friend> friends, UserRepository userRepository, LifecycleOwner lifecycleOwner, FriendViewModel friendViewModel, UserViewModel userViewModel) {
        this.friends = new ArrayList<>(friends); // Create a copy of the friends list to prevent direct modifications
        this.fullFriendsList = new ArrayList<>(friends); // Store the full list of friends (unfiltered)
        this.userRepository = userRepository;
        this.lifecycleOwner = lifecycleOwner;
        this.friendViewModel = friendViewModel;
        this.userViewModel = userViewModel;
    }

    // ViewHolder class that holds references to views for each item in the RecyclerView
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView userIconImageView; // ImageView to display the user's profile icon
        public TextView usernameTextView; // TextView to display the user's username
        public Button addFriendButton; // Button to add friend (which will be hidden)

        // Constructor to bind views
        public FriendViewHolder(View itemView) {
            super(itemView);
            userIconImageView = itemView.findViewById(R.id.user_icon); // Initialize the ImageView for the profile icon
            usernameTextView = itemView.findViewById(R.id.username_text); // Initialize the TextView for the username
            addFriendButton = itemView.findViewById(R.id.add_friend_button); // Initialize the Add Friend button
        }
    }

    // Creates and returns a new ViewHolder for each item in the RecyclerView
    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new FriendViewHolder(view); // Return the ViewHolder with the inflated view
    }

    // Binds the data for a friend to the ViewHolder at a given position in the list
    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        // Get the friend object at the given position
        Friend friend = friends.get(position);
        bindFriend(holder, friend); // Bind the data of the friend to the ViewHolder

        // Set the visibility of the 'add_friend_button' to GONE (hide it)
        holder.addFriendButton.setVisibility(View.GONE); // Hide the button

        // Set click listener to navigate to ChatActivity when an item is clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("friendId", friend.userId); // Pass the friend's ID
            v.getContext().startActivity(intent); // Start ChatActivity
        });
    }

    // Helper method to bind a friend's data to the ViewHolder
    private void bindFriend(FriendViewHolder holder, Friend friend) {

        // Observe the user data from the UserRepository using LiveData
        userRepository.getUserById(friend.userId).observe(lifecycleOwner, user -> {
            if (user != null) {
                // Set the username TextView with the user's name
                holder.usernameTextView.setText(user.username);

                // Decode the user's profile icon (Base64) into a Bitmap and set it to the ImageView
                Bitmap iconBitmap = ImageUtil.decodeFromBase64(user.userIcon);
                if (iconBitmap != null) {
                    // Set the decoded image as the user's profile picture
                    holder.userIconImageView.setImageBitmap(iconBitmap);
                } else {
                    // Set a default profile icon if decoding fails
                    holder.userIconImageView.setImageResource(R.drawable.user_icon);
                }

                // Make the profile icon visible after it's loaded
                holder.userIconImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    // Returns the total number of friends in the list
    @Override
    public int getItemCount() {
        return friends.size(); // Return the size of the friend list (number of items in RecyclerView)
    }

    // Method to update the list of friends in the adapter and notify the RecyclerView to refresh
    public void updateFriends(List<Friend> newFriends) {
        if (newFriends != null) {
            // Update the friends list with new data and make a copy to prevent external modifications
            this.friends = new ArrayList<>(newFriends);
            this.fullFriendsList = new ArrayList<>(newFriends); // Also update the full list
            notifyDataSetChanged(); // Notify the RecyclerView that the data has changed and it needs to refresh
        }
    }

    // Method to filter friends by name
    public void filterUsers(String query) {
        if (query == null || query.isEmpty()) {
            // If query is empty, show the full list
            friends = new ArrayList<>(fullFriendsList); // Reset the list to the full list
            notifyDataSetChanged();
        } else {
            // Create a filtered list that will hold matching friends
            List<Friend> filteredList = new ArrayList<>();

            // For each friend, check if their username matches the query
            for (Friend friend : fullFriendsList) { // Use the full list for filtering
                userViewModel.getUserById(friend.userId).observe(lifecycleOwner, user -> {
                    // Check if the username matches the search query (case-insensitive)
                    if (user != null && user.username.toLowerCase().contains(query.toLowerCase())) {
                        // If it matches, add the friend to the filtered list
                        filteredList.add(friend);
                    }

                    // After the loop finishes, update the adapter with the filtered list
                    friends.clear();
                    friends.addAll(filteredList);
                    notifyDataSetChanged();
                });
            }
        }
    }
}