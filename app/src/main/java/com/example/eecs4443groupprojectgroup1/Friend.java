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

    // Constructor to initialize friend relationship
    public Friend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}