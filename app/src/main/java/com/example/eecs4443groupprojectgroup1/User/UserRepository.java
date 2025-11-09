package com.example.eecs4443groupprojectgroup1.User;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.eecs4443groupprojectgroup1.db.AppDatabase;

public class UserRepository {

    private UserDao userDao;

    // Constructor to initialize the repository, getting the UserDao instance from the database
    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);  // Get the database instance
        userDao = db.userDao();  // Get the UserDao for database operations
    }

    // Login: Returns a LiveData of the user that matches the given Header and password
    public LiveData<User> login(String username, String password) {
        return userDao.login(username, password);  // Query the database for a matching user
    }

    // Get user by Header: Returns a LiveData of the user matching the given Header
    public LiveData<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);  // Query the database for a user with the given Header
    }

    // Get user by ID: Returns a LiveData of the user matching the given userId
    public LiveData<User> getUserById(int userId) {
        return userDao.getUserById(userId);  // Query the database for a user with the given ID
    }

    public String getUsernameByIdSync(int userId) {
        return userDao.getUsernameByIdSync(userId);
    }

    // Insert a new user: Runs the insertion in a separate thread to avoid blocking the main thread
    public void insert(User user) {
        new Thread(() -> userDao.insert(user)).start();  // Execute the insert operation in a background thread
    }

    // Update the Header for the given user (by userid)
    public void updateUsername(int userid, String newUsername) {
        new Thread(() -> userDao.updateUsername(userid, newUsername)).start();
    }

    // Update the password for the given user (by userid)
    public void updatePassword(int userid, String password) {
        new Thread(() -> userDao.updatePassword(userid, password)).start();
    }

    // Update the email for the given user (by userid)
    public void updateEmail(int userid, String email) {
        new Thread(() -> userDao.updateEmail(userid, email)).start();
    }

    // Update the date of birth for the given user (by userid)
    public void updateDateOfBirth(int userid, String dateOfBirth) {
        new Thread(() -> userDao.updateDateOfBirth(userid, dateOfBirth)).start();
    }

    // Update the gender for the given user (by userid)
    public void updateGender(int userid, String gender) {
        new Thread(() -> userDao.updateGender(userid, gender)).start();
    }

    // Update the description for the given user (by userid)
    public void updateDescription(int userid, String description) {
        new Thread(() -> userDao.updateDescription(userid, description)).start();
    }

    // Update the user icon (URI or Base64 string) for the given user (by userId)
    public void updateUserIconById(int userId, String userIcon) {
        new Thread(() -> userDao.updateUserIconById(userId, userIcon)).start();
    }
}