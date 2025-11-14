package com.example.eecs4443groupprojectgroup1;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private ChatRepository chatRepository;
    private MutableLiveData<Boolean> messageSentStatus = new MutableLiveData<>();

    public ChatViewModel(Application application) {
        super(application);
        chatRepository = new ChatRepository(application);
    }

    // Insert chat message into the database via the repository
    public void sendMessage(Chat chat) {
        chatRepository.insert(chat);
        // Set the status to true after the message is inserted
        messageSentStatus.setValue(true); // This can be observed in the UI for confirmation
    }

    // get latest chat Lists for a given user
    public LiveData<List<Chat>> getLatestChatListsForUser(int userId) {
        return chatRepository.getLatestChatListsForUser(userId);
    }

    // Returns the count of unread messages between userId and friendId
    public LiveData<Integer> getUnreadMessageCount(int userId, int friendId) {
        return chatRepository.getUnreadMessageCount(userId, friendId);
    }

    // Mark Messages as Read
    public void markMessagesAsRead(int userId, int friendId) {
        chatRepository.markMessagesAsRead(userId, friendId);
    }

    // Get all chat messages between current user and a friend
    public LiveData<List<Chat>> getChatBetweenUsers(int userId, int friendId) {
        return chatRepository.getChatBetweenUsers(userId, friendId);
    }
}