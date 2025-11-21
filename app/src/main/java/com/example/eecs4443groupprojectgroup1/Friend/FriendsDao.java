package com.example.eecs4443groupprojectgroup1.Friend;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendsDao {

    // Add a friend relationship (userId and friendId) with 'pending' status
    @Insert
    void addFriend(Friend friend);

    // Remove a friend relationship (userId and friendId)
    @Delete
    void removeFriend(Friend friend);

    // Get all friends of a specific user
    @Query("SELECT friend_id FROM friends WHERE user_id = :userId AND status = 'accepted'")
    List<Integer> getFriends(int userId);

    // Get the count of common friends between two users
    @Query("SELECT COUNT(DISTINCT f1.friend_id) FROM friends f1 " +
            "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = :userId1 AND f2.user_id = :userId2 AND f1.status = 'accepted' AND f2.status = 'accepted'")
    int getCommonFriendsCount(int userId1, int userId2);

    // Get the status of the friend request between user1 and user2 (returning LiveData for async)
    @Query("SELECT status FROM friends WHERE user_id = :userId1 AND friend_id = :userId2 LIMIT 1")
    LiveData<String> getFriendRequestStatus(int userId1, int userId2);

    // Check FriendRequest exist or not
    @Query("SELECT * FROM friends WHERE user_id = :userId AND friend_id = :friendId LIMIT 1")
    Friend getFriendRequest(int userId, int friendId);

    // Update the status of the friend request (accepted or rejected)
    @Query("UPDATE friends SET status = :status WHERE user_id = :userId1 AND friend_id = :userId2")
    void updateFriendRequestStatus(int userId1, int userId2, String status);

    // Fetches the list of friend requests received by the user, filtered by the provided status.
    @Query("SELECT * FROM friends WHERE friend_id = :userId AND status = :status")
    LiveData<List<Friend>> getReceivedFriendRequestsByStatus(int userId, String status);

    // Sorted version
    @Query("SELECT f.* \n" +
            "    FROM friends f\n" +
            "    INNER JOIN users u ON f.user_id = u.id\n" +
            "    WHERE f.friend_id = :userId AND f.status = :status\n" +
            "    ORDER BY u.username COLLATE NOCASE ASC")
    LiveData<List<Friend>> getReceivedFriendRequestsByStatusSorted(int userId, String status);

    // Check if there is an 'accepted' friend relationship between two users
    @Query("SELECT COUNT(*) FROM friends WHERE ((user_id = :userId AND friend_id = :friendId) OR (user_id = :friendId AND friend_id = :userId)) AND status = 'accepted'")
    boolean isFriendAccepted(int userId, int friendId);
}