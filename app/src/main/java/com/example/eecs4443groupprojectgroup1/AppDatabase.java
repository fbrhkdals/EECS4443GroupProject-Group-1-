package com.example.eecs4443groupprojectgroup1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Abstract method that returns the UserDao instance
    public abstract UserDao userDao();

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
                            //.addMigrations(Migrations.MIGRATION)
                            .build();
                }
            }
        }
        // Return the singleton database instance
        return INSTANCE;
    }
}