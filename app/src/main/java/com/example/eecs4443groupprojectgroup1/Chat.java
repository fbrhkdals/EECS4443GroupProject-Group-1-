package com.example.eecs4443groupprojectgroup1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Entity for storing chat messages
@Entity(
        tableName = "chats",
        foreignKeys = {
                @ForeignKey(
                        entity = com.example.eecs4443groupprojectgroup1.User.User.class,
                        parentColumns = "id",
                        childColumns = "sender_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = com.example.eecs4443groupprojectgroup1.User.User.class,
                        parentColumns = "id",
                        childColumns = "receiver_id",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"sender_id", "receiver_id"})}
)
public class Chat {

    @ColumnInfo(name = "message_id")
    @PrimaryKey(autoGenerate = true)
    public int messageId;

    @ColumnInfo(name = "sender_id")
    public int senderId; // ID of the user who sent the message (references User.id)

    @ColumnInfo(name = "receiver_id")
    public int receiverId; // ID of the user who received the message (references User.id)

    @ColumnInfo(name = "message")
    public String message; // The message content

    @ColumnInfo(name = "timestamp")
    public long timestamp; // The time the message was sent (e.g., System.currentTimeMillis())

    @ColumnInfo(name = "is_read")
    public boolean isRead; // Whether the message has been read or not

    @ColumnInfo(name = "message_type")
    public String messageType; // Message type (e.g., "text", "image", "file")

    // Default constructor (required by Room)
    public Chat() {}

    // Constructor for creating a new Chat object
    public Chat(int senderId, int receiverId, String message, boolean isRead, String messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = isRead;
        this.messageType = messageType;
    }
}