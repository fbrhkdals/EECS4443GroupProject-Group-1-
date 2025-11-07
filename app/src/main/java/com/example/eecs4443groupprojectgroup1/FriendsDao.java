package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    // Update the status of the friend request (accepted or rejected)
    @Query("UPDATE friends SET status = :status WHERE user_id = :userId1 AND friend_id = :userId2")
    void updateFriendRequestStatus(int userId1, int userId2, String status);

    // Get all pending friend requests for a user (friends have sent a request to user)
    @Query("SELECT * FROM friends WHERE friend_id = :userId AND status = 'pending'")
    LiveData<List<Friend>> getReceivedRequests(int userId);

    // Get all friend requests sent by a user
    @Query("SELECT * FROM friends WHERE user_id = :userId")
    LiveData<List<Friend>> getSentRequests(int userId);

    // Get all friend requests with pending status for a user
    @Query("SELECT * FROM friends WHERE user_id = :userId AND status = 'pending'")
    LiveData<List<Friend>> getPendingRequests(int userId);
}