package com.example.eecs4443groupprojectgroup1.Friend;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(
        tableName = "friends",
        foreignKeys = {
                @ForeignKey(
                        entity = com.example.eecs4443groupprojectgroup1.User.User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = com.example.eecs4443groupprojectgroup1.User.User.class,
                        parentColumns = "id",
                        childColumns = "friend_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        primaryKeys = {"user_id","friend_id"},
        indices = { @Index(value = {"user_id","friend_id"}, unique = true) }
)
public class Friend {

    // user_id and friend_id represent the relationship between two users
    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "friend_id")
    public int friendId;

    // The status of the friend request (e.g., "pending", "accepted", "rejected")
    @ColumnInfo(name = "status")
    public String status;

    // Header fields for RecyclerView
    @Ignore
    public boolean isHeader = false;

    @Ignore
    public String Header;

    // Default constructor (required for creating header items)
    public Friend() {
    }

    // Constructor to initialize friend relationship
    public Friend(int userId, int friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }
}