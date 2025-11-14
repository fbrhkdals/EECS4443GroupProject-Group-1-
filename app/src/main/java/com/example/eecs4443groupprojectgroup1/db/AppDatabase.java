package com.example.eecs4443groupprojectgroup1.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eecs4443groupprojectgroup1.Chat.Chat;
import com.example.eecs4443groupprojectgroup1.Chat.ChatDao;
import com.example.eecs4443groupprojectgroup1.Friend.FriendsDao;
import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserDao;
import com.example.eecs4443groupprojectgroup1.Friend.Friend;

@Database(entities = {User.class, Friend.class, Chat.class}, version = 1) // Added Friend.class here
public abstract class AppDatabase extends RoomDatabase {

    // Abstract methods to get DAO instances
    public abstract UserDao userDao();
    public abstract FriendsDao friendDao();
    public abstract ChatDao chatDao();

    // Singleton instance to ensure only one instance of the database is created
    private static volatile AppDatabase INSTANCE;

    // Method to get the singleton instance of the database
    public static AppDatabase getInstance(Context context) {
        // Check if INSTANCE is null
        if (INSTANCE == null) {
            // Synchronized block to ensure thread safety while creating the instance
            synchronized (AppDatabase.class) {
                // Double check if INSTANCE is still null before creating the database
                if (INSTANCE == null) {
                    // Create the database instance using Room
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            //.addMigrations(Migrations.MIGRATION) // Add migration if schema changes
                            .build();
                }
            }
        }
        // Return the singleton database instance
        return INSTANCE;
    }
}