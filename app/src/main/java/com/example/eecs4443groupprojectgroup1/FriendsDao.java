package com.example.eecs4443groupprojectgroup1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendsDao {

    // Add a friend relationship (userId and friendId)
    @Insert
    void addFriend(Friend friend);

    // Remove a friend relationship (userId and friendId)
    @Delete
    void removeFriend(Friend friend);

    // Get all friends of a specific user
    @Query("SELECT friend_id FROM friends WHERE user_id = :userId")
    List<Integer> getFriends(int userId);

    // Get the count of common friends between two users
    @Query("SELECT COUNT(DISTINCT f1.friend_id) FROM friends f1 " +
            "JOIN friends f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = :userId1 AND f2.user_id = :userId2")
    int getCommonFriendsCount(int userId1, int userId2);
}