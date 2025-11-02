package com.example.eecs4443groupprojectgroup1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    // Insert a new user into the database
    @Insert
    void insert(User user);

    // Login: Find a user where the username and password match
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) AND password = :password LIMIT 1")
    LiveData<User> login(String username, String password);

    // Get user by username: Find a user by their username
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    // Update username
    @Query("UPDATE users SET username = :username WHERE username = :currentUsername")
    void updateUsername(String currentUsername, String username);

    // Update password
    @Query("UPDATE users SET password = :password WHERE username = :username")
    void updatePassword(String username, String password);

    // Update email
    @Query("UPDATE users SET email = :email WHERE username = :username")
    void updateEmail(String username, String email);

    // Update dateOfBirth
    @Query("UPDATE users SET dateOfBirth = :dateOfBirth WHERE username = :username")
    void updateDateOfBirth(String username, String dateOfBirth);

    // Update gender
    @Query("UPDATE users SET gender = :gender WHERE username = :username")
    void updateGender(String username, String gender);

    // Update description
    @Query("UPDATE users SET description = :description WHERE username = :username")
    void updateDescription(String username, String description);

    // Update user icon (URI or Base64 string)
    @Query("UPDATE users SET userIcon = :userIcon WHERE username = :username")
    void updateUserIcon(String username, String userIcon);
}