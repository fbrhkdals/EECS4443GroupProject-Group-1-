package com.example.eecs4443groupprojectgroup1;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<FriendRepository.UserWithCommonFriends> usersWithCommonFriends;

    // Constructor to initialize list
    public UserAdapter(List<FriendRepository.UserWithCommonFriends> usersWithCommonFriends) {
        this.usersWithCommonFriends = usersWithCommonFriends;
    }

    // ViewHolder class to bind each item (user) in the list
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public ImageView userIconImageView;

        public UserViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text);
            userIconImageView = itemView.findViewById(R.id.user_icon);
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
        FriendRepository.UserWithCommonFriends userWithCommonFriends = usersWithCommonFriends.get(position);

        // Set username text
        holder.usernameTextView.setText(userWithCommonFriends.user.username);

        // Convert the Base64 string to Bitmap and set to ImageView
        String base64Image = userWithCommonFriends.user.userIcon; // Assuming user has iconBase64 field
        Bitmap userIcon = ImageUtil.decodeFromBase64(base64Image);  // Decode the Base64 image

        // If the image is valid, set it to the ImageView
        if (userIcon != null) {
            holder.userIconImageView.setImageBitmap(userIcon);
        } else {
            // If no valid image, set a default placeholder or empty
            holder.userIconImageView.setImageResource(R.drawable.user_icon);  // Default placeholder
        }
    }

    // Return the total number of items (users) in the list
    @Override
    public int getItemCount() {
        return usersWithCommonFriends != null ? usersWithCommonFriends.size() : 0;
    }

    // Method to update the list of users and notify the adapter to refresh the UI
    public void updateUsers(List<FriendRepository.UserWithCommonFriends> newUsers) {
        this.usersWithCommonFriends = newUsers;
        notifyDataSetChanged();
    }
}