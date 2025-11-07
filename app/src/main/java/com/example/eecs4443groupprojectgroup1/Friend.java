package com.example.eecs4443groupprojectgroup1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends")
public class Friend {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // user_id and friend_id represent the relationship between two users
    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "friend_id")
    public int friendId;

    // The status of the friend request (e.g., "pending", "accepted", "rejected")
    @ColumnInfo(name = "status")
    public String status;

    // Constructor to initialize friend relationship
    public Friend(int userId, int friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }
}