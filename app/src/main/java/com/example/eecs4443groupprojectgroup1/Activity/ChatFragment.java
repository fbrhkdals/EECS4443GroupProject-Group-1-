package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eecs4443groupprojectgroup1.Chat.Chat;
import com.example.eecs4443groupprojectgroup1.Adapter.ChatAdapter;
import com.example.eecs4443groupprojectgroup1.Chat.ChatViewModel;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ChatViewModel chatViewModel;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize view models and RecyclerView
        initializeViews(view);

        // Get current user ID from shared preferences
        int currentUserId = SharedPreferencesHelper.getUserId(getContext());

        // Observe the latest chat lists for the current user
        observeChatList(currentUserId);

        return view;
    }

    private void initializeViews(View view) {
        // Initialize RecyclerView and layout manager
        recyclerView = view.findViewById(R.id.chats_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ViewModels
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize the chat adapter
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<>(), userViewModel, chatViewModel, this::onChatItemClick);
        recyclerView.setAdapter(chatAdapter);
    }

    private void observeChatList(int currentUserId) {
        // Observe the chat list for the current user and update the adapter
        chatViewModel.getLatestChatListsForUser(currentUserId).observe(getViewLifecycleOwner(), this::updateChatList);
    }

    private void updateChatList(List<Chat> chats) {
        // Update the chat list in the adapter
        chatAdapter.setChatList(chats);
    }

    private void onChatItemClick(int friendId) {
        // Handle chat item click and start ChatActivity
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("friendId", friendId);  // Pass the friend ID to ChatActivity
        startActivity(intent);
    }
}