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

import com.example.eecs4443groupprojectgroup1.Chat;
import com.example.eecs4443groupprojectgroup1.ChatActivity;
import com.example.eecs4443groupprojectgroup1.ChatAdapter;
import com.example.eecs4443groupprojectgroup1.ChatViewModel;
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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize ChatViewModel and UserViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.chats_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize ChatAdapter with an empty list and pass userViewModel
        chatAdapter = new ChatAdapter(getContext(), new ArrayList<>(), userViewModel, chatViewModel, friendId -> {
            // Handle item click and start ChatActivity with friendId
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("friendId", friendId);  // Pass the friendId to ChatActivity
            startActivity(intent);
        });
        recyclerView.setAdapter(chatAdapter);

        // Get current user id
        int currentUserId = SharedPreferencesHelper.getUserId(getContext());

        // Observe the chat list for the current user
        chatViewModel.getLatestChatListsForUser(currentUserId).observe(getViewLifecycleOwner(), new Observer<List<Chat>>() {
            @Override
            public void onChanged(List<Chat> chats) {
                // Update the adapter with the latest chat list
                chatAdapter.setChatList(chats);
            }
        });

        return view;
    }
}