package com.example.eecs4443groupprojectgroup1;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LoginViewModel extends AndroidViewModel {

    // Declare a UserRepository to interact with the database
    private UserRepository userRepository;

    // Constructor that initializes the UserRepository
    public LoginViewModel(Application application) {
        super(application);
        // Initialize the repository that provides data to the ViewModel
        userRepository = new UserRepository(application);
    }

    // Attempt login: Returns LiveData<User> which can be observed by the UI (Activity/Fragment)
    public LiveData<User> login(String username, String password) {
        // Call the repository's login method to check credentials and get user data
        return userRepository.login(username, password);
    }
}