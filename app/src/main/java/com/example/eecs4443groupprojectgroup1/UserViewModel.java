package com.example.eecs4443groupprojectgroup1;

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

    // Get user by username (To check duplication)
    public LiveData<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    // Insert new User
    public void insert(User user) {
        userRepository.insert(user);
    }

    // Update the username of a specific user by username
    public void updateUsername(String username, String newUsername) {
        userRepository.updateUsername(username, newUsername);
    }

    // Update the password of a specific user by username
    public void updatePassword(String username, String password) {
        userRepository.updatePassword(username, password);
    }

    // Update the email of a specific user by username
    public void updateEmail(String username, String email) {
        userRepository.updateEmail(username, email);
    }

    // Update the date of birth of a specific user by username
    public void updateDateOfBirth(String username, String dateOfBirth) {
        userRepository.updateDateOfBirth(username, dateOfBirth);
    }

    // Update the gender of a specific user by username
    public void updateGender(String username, String gender) {
        userRepository.updateGender(username, gender);
    }

    // Update the description of a specific user by username
    public void updateDescription(String username, String description) {
        userRepository.updateDescription(username, description);
    }

    // Update the user icon (URI or Base64) of a specific user by username
    public void updateUserIcon(String username, String userIcon) {
        userRepository.updateUserIcon(username, userIcon);
    }
}