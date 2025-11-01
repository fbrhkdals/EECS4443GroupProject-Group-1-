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
    @Query("UPDATE users SET username = :username WHERE id = :id")
    void updateUsername(int id, String username);

    // Update password
    @Query("UPDATE users SET password = :password WHERE id = :id")
    void updatePassword(int id, String password);

    // Update email
    @Query("UPDATE users SET email = :email WHERE id = :id")
    void updateEmail(int id, String email);

    // Update dateOfBirth
    @Query("UPDATE users SET dateOfBirth = :dateOfBirth WHERE id = :id")
    void updateDateOfBirth(int id, String dateOfBirth);

    // Update gender
    @Query("UPDATE users SET gender = :gender WHERE id = :id")
    void updateGender(int id, String gender);

    // Update description
    @Query("UPDATE users SET description = :description WHERE id = :id")
    void updateDescription(int id, String description);

    // Update user icon (URI or Base64 string)
    @Query("UPDATE users SET userIcon = :userIcon WHERE id = :id")
    void updateUserIcon(int id, String userIcon);
}