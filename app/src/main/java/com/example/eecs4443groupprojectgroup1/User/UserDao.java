package com.example.eecs4443groupprojectgroup1.User;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    // Insert a new user into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // Login: Find a user where the Header and password match
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) AND password = :password LIMIT 1")
    LiveData<User> login(String username, String password);

    // Get user by Header: Find a user by their Header
    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    LiveData<User> getUserByUsername(String username);

    // Get user by ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);

    @Query("SELECT username FROM users WHERE id = :userId LIMIT 1")
    String getUsernameByIdSync(int userId);

    // Update Header by userId
    @Query("UPDATE users SET username = :username WHERE id = :userId")
    void updateUsername(int userId, String username);

    // Update password by userId
    @Query("UPDATE users SET password = :password WHERE id = :userId")
    void updatePassword(int userId, String password);

    // Update email by userId
    @Query("UPDATE users SET email = :email WHERE id = :userId")
    void updateEmail(int userId, String email);

    // Update dateOfBirth by userId
    @Query("UPDATE users SET dateOfBirth = :dateOfBirth WHERE id = :userId")
    void updateDateOfBirth(int userId, String dateOfBirth);

    // Update gender by userId
    @Query("UPDATE users SET gender = :gender WHERE id = :userId")
    void updateGender(int userId, String gender);

    // Update description by userId
    @Query("UPDATE users SET description = :description WHERE id = :userId")
    void updateDescription(int userId, String description);

    // Update user icon (URI or Base64 string) by userId
    @Query("UPDATE users SET userIcon = :userIcon WHERE id = :userId")
    void updateUserIconById(int userId, String userIcon);

    // Get user by sync
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserByIdSync(int userId);

    // Get all users: Retrieve a list of all users in the database
    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}