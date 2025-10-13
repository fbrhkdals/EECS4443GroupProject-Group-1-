package com.example.eecs4443groupprojectgroup1;

import android.content.Context;

import androidx.lifecycle.LiveData;

public class UserRepository {

    private UserDao userDao;

    // Constructor to initialize the repository, getting the UserDao instance from the database
    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);  // Get the database instance
        userDao = db.userDao();  // Get the UserDao for database operations
    }

    // Login: Returns a LiveData of the user that matches the given username and password
    public LiveData<User> login(String username, String password) {
        return userDao.login(username, password);  // Query the database for a matching user
    }

    // Get user by username: Returns a LiveData of the user matching the given username
    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);  // Query the database for a user with the given username
    }

    // Insert a new user: Runs the insertion in a separate thread to avoid blocking the main thread
    public void insert(User user) {
        new Thread(() -> userDao.insert(user)).start();  // Execute the insert operation in a background thread
    }
}