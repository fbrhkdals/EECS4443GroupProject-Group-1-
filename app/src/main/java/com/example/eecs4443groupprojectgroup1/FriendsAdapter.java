package com.example.eecs4443groupprojectgroup1;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0; // View type for headers
    private static final int TYPE_FRIEND = 1; // View type for normal friend items

    private List<Friend> friends; // List of friends and headers
    private UserRepository userRepository; // Repository to fetch user details
    private FriendViewModel friendViewModel; // ViewModel to manage friend updates
    private LifecycleOwner lifecycleOwner; // Needed for observing LiveData

    public FriendsAdapter(List<Friend> friends, UserRepository userRepository, LifecycleOwner lifecycleOwner, FriendViewModel friendViewModel) {
        this.friends = new ArrayList<>(friends); // Make a copy of the initial list
        this.userRepository = userRepository;
        this.lifecycleOwner = lifecycleOwner;
        this.friendViewModel = friendViewModel;
    }

    // ViewHolder for headers (e.g., "Friend Requests", "Friends")
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView headerText;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_text); // TextView for the header title
        }
    }

    // ViewHolder for normal friend items
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView userIconImageView; // User profile picture
        public TextView usernameTextView; // Username
        public ImageButton acceptButton; // Button to accept friend request
        public ImageButton declineButton; // Button to decline friend request

        public FriendViewHolder(View itemView) {
            super(itemView);
            userIconImageView = itemView.findViewById(R.id.user_icon);
            usernameTextView = itemView.findViewById(R.id.username_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Determine if the current item is a header or a friend item
        return friends.get(position).isHeader ? TYPE_HEADER : TYPE_FRIEND;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on view type
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item, parent, false);
            return new FriendViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Friend friend = friends.get(position);

        if (getItemViewType(position) == TYPE_HEADER) {
            // Bind header text
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerText.setText(friend.Header);
        } else {
            // Bind friend data
            FriendViewHolder friendHolder = (FriendViewHolder) holder;
            bindFriend(friendHolder, friend);
        }
    }

    /**
     * Binds a Friend item to its ViewHolder
     */
    private void bindFriend(FriendViewHolder holder, Friend friend) {
        holder.userIconImageView.setVisibility(View.INVISIBLE); // Hide icon initially

        // Observe LiveData for user details
        userRepository.getUserById(friend.userId).observe(lifecycleOwner, user -> {
            if (user != null) {
                holder.usernameTextView.setText(user.username); // Set username
                Bitmap iconBitmap = ImageUtil.decodeFromBase64(user.userIcon); // Decode profile icon
                if (iconBitmap != null) holder.userIconImageView.setImageBitmap(iconBitmap);
                else holder.userIconImageView.setImageResource(R.drawable.user_icon); // Default icon
                holder.userIconImageView.setVisibility(View.VISIBLE); // Show icon

                // Show popup dialog when list item is clicked
                holder.itemView.setOnClickListener(v -> {
                    View popupView = LayoutInflater.from(v.getContext())
                            .inflate(R.layout.user_detail_popup, null);

                    ImageView popupIcon = popupView.findViewById(R.id.popup_user_icon);
                    TextView popupUsername = popupView.findViewById(R.id.popup_username);
                    TextView popupDescription = popupView.findViewById(R.id.popup_description);
                    TextView popupEmail = popupView.findViewById(R.id.popup_email);
                    TextView popupBirthday = popupView.findViewById(R.id.popup_birthday);
                    TextView popupGender = popupView.findViewById(R.id.popup_gender);
                    Button popupDelete = popupView.findViewById(R.id.delete_button);
                    Button popupCreateChat = popupView.findViewById(R.id.create_chat_button);

                    // Default text handling
                    String descriptionText = (user.description == null || user.description.trim().isEmpty())
                            ? "User has no description."
                            : user.description;
                    String birthdayText = (user.dateOfBirth == null || user.dateOfBirth.trim().isEmpty())
                            ? "Birthday not set"
                            : user.dateOfBirth;
                    String genderText = (user.gender == null || user.gender.trim().isEmpty())
                            ? "Unspecified"
                            : user.gender;

                    popupUsername.setText(user.username);
                    popupDescription.setText(descriptionText);
                    popupEmail.setText(user.email);
                    popupBirthday.setText(birthdayText);
                    popupGender.setText(genderText);

                    if (iconBitmap != null)
                        popupIcon.setImageBitmap(iconBitmap);
                    else
                        popupIcon.setImageResource(R.drawable.user_icon);

                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setView(popupView)
                            .create();

                    Executors.newSingleThreadExecutor().execute(() -> {

                        Friend existing = friendViewModel.getFriendRequest(friend.userId, friend.friendId);

                        if (existing != null && "accepted".equals(existing.status)){
                            // Delete friend
                            popupDelete.setOnClickListener(bv -> {
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "deleted");
                                friendViewModel.updateFriendRequestStatus(friend.friendId, friend.userId, "deleted");
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    friends.remove(pos);
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(v.getContext(), "Friend deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });

                            // Create chat
                            popupCreateChat.setOnClickListener(bv -> {
                                // Pass friend's information in the intent and start ChatActivity
                                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                                intent.putExtra("friendId", friend.userId); // Pass the friend's ID
                                v.getContext().startActivity(intent); // Start ChatActivity
                                Toast.makeText(v.getContext(), "Chat created with " + user.username, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        } else {

                            popupDelete.setText("Decline");
                            popupCreateChat.setText("Accept");

                            // Accept
                            popupCreateChat.setOnClickListener(bv -> {
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "accepted");
                                friendViewModel.updateFriendRequestStatus(friend.friendId, friend.userId, "accepted");
                                Toast.makeText(v.getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });

                            // Decline
                            popupDelete.setOnClickListener(bv -> {
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "rejected");
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    friends.remove(pos);
                                    notifyItemRemoved(pos);
                                    Toast.makeText(v.getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    dialog.show();
                });
            }
        });

        // Show accept/decline buttons only for pending friend requests
        if ("pending".equals(friend.status)) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
        } else {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }

        // Accept button click listener
        holder.acceptButton.setOnClickListener(v -> {
            friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "accepted");
            friendViewModel.updateFriendRequestStatus(friend.friendId, friend.userId, "accepted");
            Toast.makeText(v.getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
        });

        // Decline button click listener
        holder.declineButton.setOnClickListener(v -> {
            friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "rejected");
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                friends.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size(); // Total number of items (headers + friends)
    }

    /**
     * Updates the adapter's data and refreshes the RecyclerView
     */
    public void updateFriends(List<Friend> newFriends) {
        if (newFriends != null) {
            this.friends = new ArrayList<>(newFriends); // Copy the new list
            notifyDataSetChanged(); // Refresh RecyclerView
        }
    }
}