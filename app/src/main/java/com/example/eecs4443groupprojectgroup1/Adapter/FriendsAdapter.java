package com.example.eecs4443groupprojectgroup1.Adapter;

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

import com.example.eecs4443groupprojectgroup1.Activity.ChatActivity;
import com.example.eecs4443groupprojectgroup1.Chat.ChatViewModel;
import com.example.eecs4443groupprojectgroup1.Friend.Friend;
import com.example.eecs4443groupprojectgroup1.Friend.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0; // View type for headers (e.g., "Friend Requests", "Friends")
    private static final int TYPE_FRIEND = 1; // View type for normal friend items

    private List<Friend> friends; // List of friends and headers
    private UserRepository userRepository; // Repository to fetch user details
    private FriendViewModel friendViewModel; // ViewModel to manage friend updates
    private ChatViewModel chatViewModel; // ViewModel to handle chat operations

    private LifecycleOwner lifecycleOwner; // LifecycleOwner for observing LiveData

    public FriendsAdapter(List<Friend> friends, UserRepository userRepository, LifecycleOwner lifecycleOwner, FriendViewModel friendViewModel, ChatViewModel chatViewModel) {
        this.friends = new ArrayList<>(friends); // Make a copy of the initial list
        this.userRepository = userRepository;
        this.lifecycleOwner = lifecycleOwner;
        this.friendViewModel = friendViewModel;
        this.chatViewModel = chatViewModel;
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
            return new HeaderViewHolder(view); // Return ViewHolder for header
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_item, parent, false);
            return new FriendViewHolder(view); // Return ViewHolder for friend item
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Friend friend = friends.get(position);

        if (getItemViewType(position) == TYPE_HEADER) {
            // Bind header text for "Friend Requests", "Friends", etc.
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
     * This method handles the display of user data, profile image, and action buttons (accept/decline)
     */
    private void bindFriend(FriendViewHolder holder, Friend friend) {
        holder.userIconImageView.setVisibility(View.INVISIBLE); // Initially hide the user icon

        // Observe LiveData for user details from the UserRepository
        userRepository.getUserById(friend.userId).observe(lifecycleOwner, user -> {
            if (user != null) {
                holder.usernameTextView.setText(user.username); // Set the username
                Bitmap iconBitmap = ImageUtil.decodeFromBase64(user.userIcon); // Decode profile icon
                if (iconBitmap != null) {
                    holder.userIconImageView.setImageBitmap(iconBitmap); // Set decoded image
                } else {
                    holder.userIconImageView.setImageResource(R.drawable.user_icon); // Default icon if decoding fails
                }
                holder.userIconImageView.setVisibility(View.VISIBLE); // Show the icon

                // Show popup dialog when the list item is clicked
                holder.itemView.setOnClickListener(v -> {
                    View popupView = LayoutInflater.from(v.getContext())
                            .inflate(R.layout.user_detail_popup, null);

                    // Set up the UI components inside the popup
                    ImageView popupIcon = popupView.findViewById(R.id.popup_user_icon);
                    TextView popupUsername = popupView.findViewById(R.id.popup_username);
                    TextView popupDescription = popupView.findViewById(R.id.popup_description);
                    TextView popupEmail = popupView.findViewById(R.id.popup_email);
                    TextView popupBirthday = popupView.findViewById(R.id.popup_birthday);
                    TextView popupGender = popupView.findViewById(R.id.popup_gender);
                    Button popupDelete = popupView.findViewById(R.id.delete_button);
                    Button popupCreateChat = popupView.findViewById(R.id.create_chat_button);

                    // Handle null fields for description, birthday, and gender
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

                    // Set the user icon in the popup
                    if (iconBitmap != null) popupIcon.setImageBitmap(iconBitmap);
                    else popupIcon.setImageResource(R.drawable.user_icon);

                    // Create and show the dialog
                    AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                            .setView(popupView)
                            .create();

                    Executors.newSingleThreadExecutor().execute(() -> {

                        Friend existing = friendViewModel.getFriendRequest(friend.userId, friend.friendId);

                        if (existing != null && "accepted".equals(existing.status)){
                            // If the friend request is accepted, show options to delete or create chat
                            popupDelete.setOnClickListener(bv -> {
                                // Delete friend and the chat between the users
                                chatViewModel.deleteChatBetweenUserAndFriend(friend.userId, friend.friendId);
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "deleted");
                                friendViewModel.updateFriendRequestStatus(friend.friendId, friend.userId, "deleted");
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    friends.remove(pos); // Remove the friend from the list
                                    notifyItemRemoved(pos);
                                }
                                Toast.makeText(v.getContext(), "Friend deleted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });

                            // Create a chat with the friend
                            popupCreateChat.setOnClickListener(bv -> {
                                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                                intent.putExtra("friendId", friend.userId); // Pass the friend's ID
                                v.getContext().startActivity(intent); // Start ChatActivity
                                Toast.makeText(v.getContext(), "Chat created with " + user.username, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        } else {
                            // If the friend request is pending, show options to accept or decline
                            popupDelete.setText("Decline");
                            popupCreateChat.setText("Accept");

                            // Accept button click listener
                            popupCreateChat.setOnClickListener(bv -> {
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "accepted");
                                friendViewModel.updateFriendRequestStatus(friend.friendId, friend.userId, "accepted");
                                Toast.makeText(v.getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });

                            // Decline button click listener
                            popupDelete.setOnClickListener(bv -> {
                                friendViewModel.updateFriendRequestStatus(friend.userId, friend.friendId, "rejected");
                                int pos = holder.getAdapterPosition();
                                if (pos != RecyclerView.NO_POSITION) {
                                    friends.remove(pos); // Remove the friend from the list
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
                friends.remove(pos); // Remove friend from the list
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Friend request declined", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size(); // Total number of items (headers + friends)
    }

     // Updates the adapter's data and refreshes the RecyclerView
    public void updateFriends(List<Friend> newFriends) {
        if (newFriends != null) {
            this.friends = new ArrayList<>(newFriends); // Copy the new list
            notifyDataSetChanged(); // Refresh RecyclerView
        }
    }
}