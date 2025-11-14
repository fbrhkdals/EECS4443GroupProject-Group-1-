package com.example.eecs4443groupprojectgroup1.Chat;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

import java.util.List;

public class ChatRepository {
    private ChatDao chatDao;

    // Constructor to initialize the DAO
    public ChatRepository(Context context) {
        // Get the database instance
        AppDatabase db = AppDatabase.getInstance(context);
        chatDao = db.chatDao();
    }

    // Insert message into database
    public void insert(Chat chat) {
        // Insert the chat message in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                chatDao.insert(chat);
            }
        }).start();
    }

    // Get the latest chats for a given user
    public LiveData<List<Chat>> getLatestChatListsForUser(int userId) {
        return chatDao.getLatestChatListsForUser(userId);
    }

    // Returns the count of unread messages between userId and friendId
    public LiveData<Integer> getUnreadMessageCount(int userId, int friendId) {
        return chatDao.getUnreadMessageCount(userId, friendId);
    }

    // Mark Messages as Read
    public void markMessagesAsRead(int userId, int friendId) {
        new Thread(() -> chatDao.markMessagesAsRead(userId, friendId)).start();
    }

    // Get all chat messages between current user and a friend, timestamp order
    public LiveData<List<Chat>> getChatBetweenUsers(int userId, int friendId) {
        return chatDao.getChatBetweenUsers(userId, friendId);
    }

    // Delete all chats between the current user and a friend
    public void deleteChatBetweenUserAndFriend(int userId, int friendId) {
        new Thread(() -> chatDao.deleteChatBetweenUserAndFriend(userId, friendId)).start();
    }
}