package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatDao {

    @Insert
    void insert(Chat chat);  // Save Message

    /**
     * Returns the most recent message for each chat pair (sender/receiver),
     * where the given userId is either the sender or receiver.
     * (1,2) and (2,1) are treated as the same conversation pair.
     */
    @Query("SELECT c.*, " +
            "       MAX(c.timestamp) AS latest_timestamp, " +
            "       (CASE WHEN c.receiver_id = :userId AND c.is_read = 0 THEN 1 ELSE 0 END) AS is_unread " +
            "FROM chats c " +
            "INNER JOIN ( " +
            "    SELECT " +
            "        CASE WHEN sender_id < receiver_id THEN sender_id ELSE receiver_id END AS user1, " +
            "        CASE WHEN sender_id < receiver_id THEN receiver_id ELSE sender_id END AS user2, " +
            "        MAX(timestamp) AS latest_timestamp " +
            "    FROM chats " +
            "    WHERE sender_id = :userId OR receiver_id = :userId " +
            "    GROUP BY user1, user2 " +
            ") grouped " +
            "ON (CASE WHEN c.sender_id < c.receiver_id THEN c.sender_id ELSE c.receiver_id END = grouped.user1) " +
            "AND (CASE WHEN c.sender_id < c.receiver_id THEN c.receiver_id ELSE c.sender_id END = grouped.user2) " +
            "AND c.timestamp = grouped.latest_timestamp " +
            "WHERE (c.sender_id = :userId OR c.receiver_id = :userId) " +
            "GROUP BY c.sender_id, c.receiver_id " +
            "ORDER BY is_unread DESC, latest_timestamp DESC")
    LiveData<List<Chat>> getLatestChatListsForUser(int userId);

    // Returns the count of unread messages (is_read = 0) between the given userId and friendId.
    @Query("SELECT COUNT(*) " +
            "FROM chats c " +
            "WHERE (c.sender_id = :friendId AND c.receiver_id = :userId) " +
            "   AND c.is_read = 0")
    LiveData<Integer> getUnreadMessageCount(int userId, int friendId);

    // Mark Messages as Read
    @Query("UPDATE chats SET is_read = 1 " +
            "WHERE sender_id = :friendId AND receiver_id = :userId AND is_read = 0")
    void markMessagesAsRead(int userId, int friendId);

    // Get Messages
    @Query("SELECT * FROM chats " +
            "WHERE (sender_id = :userId AND receiver_id = :friendId) " +
            "   OR (sender_id = :friendId AND receiver_id = :userId) " +
            "ORDER BY timestamp ASC")
    LiveData<List<Chat>> getChatBetweenUsers(int userId, int friendId);
}