package com.example.eecs4443groupprojectgroup1.User;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserViewModel extends AndroidViewModel {

    // Declare a UserRepository to interact with the database
    private UserRepository userRepository;

    // Constructor that initializes the UserRepository
    public UserViewModel(Application application) {
        super(application);
        // Initialize the repository that provides data to the ViewModel
        userRepository = new UserRepository(application);
    }

    // Attempt login: Returns LiveData<User> which can be observed by the UI (Activity/Fragment)
    public LiveData<User> login(String username, String password) {
        // Call the repository's login method to check credentials and get user data
        return userRepository.login(username, password);
    }

    // Get user by Header (To check duplication)
    public LiveData<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    // Get user by ID
    public LiveData<User> getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    // Insert new User
    public void insert(User user) {
        userRepository.insert(user);
    }

    // Update the Header of a specific user by id
    public void updateUsername(int userid, String newUsername) {
        userRepository.updateUsername(userid, newUsername);
    }

    // Update the password of a specific user by id
    public void updatePassword(int userid, String password) {
        userRepository.updatePassword(userid, password);
    }

    // Update the email of a specific user by id
    public void updateEmail(int userid, String email) {
        userRepository.updateEmail(userid, email);
    }

    // Update the date of birth of a specific user by id
    public void updateDateOfBirth(int userid, String dateOfBirth) {
        userRepository.updateDateOfBirth(userid, dateOfBirth);
    }

    // Update the gender of a specific user by id
    public void updateGender(int userid, String gender) {
        userRepository.updateGender(userid, gender);
    }

    // Update the description of a specific user by id
    public void updateDescription(int userid, String description) {
        userRepository.updateDescription(userid, description);
    }

    // Update the user icon (URI or Base64) of a specific user by userId
    public void updateUserIconById(int userId, String userIcon) {
        userRepository.updateUserIconById(userId, userIcon);
    }
}