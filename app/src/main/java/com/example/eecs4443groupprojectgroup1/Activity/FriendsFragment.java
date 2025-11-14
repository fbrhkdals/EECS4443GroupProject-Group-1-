package com.example.eecs4443groupprojectgroup1.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Chat.ChatViewModel;
import com.example.eecs4443groupprojectgroup1.Friend.Friend;
import com.example.eecs4443groupprojectgroup1.Friend.FriendViewModel;
import com.example.eecs4443groupprojectgroup1.Adapter.FriendsAdapter;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserRepository;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private FriendViewModel friendViewModel;
    private ChatViewModel chatViewModel;
    private UserRepository userRepository;
    private List<Friend> pendingList = new ArrayList<>();
    private List<Friend> acceptedList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.friends_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use LinearLayoutManager

        // Initialize UserRepository with Context
        userRepository = new UserRepository(getContext());

        // Initialize FriendViewModel using a custom Factory
        AppDatabase appDatabase = AppDatabase.getInstance(getContext()); // Get database instance
        FriendViewModel.Factory factoryFriend = new FriendViewModel.Factory(appDatabase);
        friendViewModel = new ViewModelProvider(this, factoryFriend).get(FriendViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize the RecyclerView adapter with an empty list for now
        friendsAdapter = new FriendsAdapter(
                new ArrayList<Friend>(), // initial empty list
                userRepository,
                getViewLifecycleOwner(),
                friendViewModel,
                chatViewModel
        );
        recyclerView.setAdapter(friendsAdapter); // Set adapter to RecyclerView

        // Get the current user's ID from shared preferences
        int userId = SharedPreferencesHelper.getUserId(getContext());

        // Observe pending friend requests
        friendViewModel.getReceivedFriendRequestsByStatus(userId, "pending")
                .observe(getViewLifecycleOwner(), pending -> {
                    // Update pending list or use empty list if null
                    pendingList = pending != null ? pending : new ArrayList<>();
                    updateCombinedList(); // Refresh the combined list in RecyclerView
                });

        // Observe accepted friend requests
        friendViewModel.getReceivedFriendRequestsByStatusSorted(userId, "accepted")
                .observe(getViewLifecycleOwner(), accepted -> {
                    // Update accepted list or use empty list if null
                    acceptedList = accepted != null ? accepted : new ArrayList<>();
                    updateCombinedList(); // Refresh the combined list in RecyclerView
                });

        return view; // Return the root view of the fragment
    }

    /**
     * Combines pending and accepted friends into a single list
     * with headers and updates the RecyclerView adapter.
     */
    private void updateCombinedList() {
        List<Friend> combined = new ArrayList<>();

        // Add pending friends section
        if (!pendingList.isEmpty()) {
            Friend pendingHeader = new Friend();
            pendingHeader.isHeader = true; // Mark as header
            pendingHeader.Header = "------"; // Set header title
            combined.addAll(pendingList); // Add pending friends
            combined.add(pendingHeader); // Add header
        }

        // Add accepted friends section
        if (!acceptedList.isEmpty()) {
            combined.addAll(acceptedList); // Add accepted friends
        }

        // Update adapter with the combined list
        friendsAdapter.updateFriends(combined);
    }
}