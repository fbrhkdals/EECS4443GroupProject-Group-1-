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

    // Get username (To check duplication)
    public LiveData<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    // Insert new User
    public void insert(User user) {
        userRepository.insert(user);
    }

    // Update the username of a specific user
    public void updateUsername(int id, String username) {
        userRepository.updateUsername(id, username);
    }

    // Update the password of a specific user
    public void updatePassword(int id, String password) {
        userRepository.updatePassword(id, password);
    }

    // Update the email of a specific user
    public void updateEmail(int id, String email) {
        userRepository.updateEmail(id, email);
    }

    // Update the date of birth of a specific user
    public void updateDateOfBirth(int id, String dateOfBirth) {
        userRepository.updateDateOfBirth(id, dateOfBirth);
    }

    // Update the gender of a specific user
    public void updateGender(int id, String gender) {
        userRepository.updateGender(id, gender);
    }

    // Update the description of a specific user
    public void updateDescription(int id, String description) {
        userRepository.updateDescription(id, description);
    }

    // Update the user icon (URI or Base64) of a specific user
    public void updateUserIcon(int id, String userIcon) {
        userRepository.updateUserIcon(id, userIcon);
    }
}
