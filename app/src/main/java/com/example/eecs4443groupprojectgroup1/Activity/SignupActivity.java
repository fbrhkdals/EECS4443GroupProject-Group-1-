package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, emailInput;  // Input fields for username, password, and email
    private MaterialButton signupBtn; // Button to trigger the signup action
    private UserViewModel userViewModel; // ViewModel to interact with user data

    private TextView usernameError, passwordError, emailError; // TextViews to show error messages
    private ImageView passwordToggleIcon; // Icon to toggle password visibility

    private boolean isPasswordVisible = false; // Flag to track password visibility
    private boolean userCreationSuccess = false; // Flag to track signup success

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        // Initialize the UI components
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        signupBtn = findViewById(R.id.signup_btn);
        passwordToggleIcon = findViewById(R.id.password_toggle_icon);

        // Initialize error message TextViews
        usernameError = findViewById(R.id.username_error);
        passwordError = findViewById(R.id.password_error);
        emailError = findViewById(R.id.email_error);

        // Initialize the ViewModel
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(UserViewModel.class);

        // Back button: navigate to MainActivity and finish SignupActivity
        findViewById(R.id.back_button).setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        });

        // Toggle password visibility on click of the eye icon
        passwordToggleIcon.setOnClickListener(v -> togglePasswordVisibility());

        // Signup button logic
        signupBtn.setOnClickListener(v -> {
            // Get user input values and trim whitespace
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            // Flags to track validation errors
            boolean isUsernameError = false;
            boolean isPasswordError = false;
            boolean isEmailError = false;

            // Validate username
            if (username.isEmpty()) {
                usernameError.setText(getString(R.string.error_username_required));
                usernameError.setVisibility(View.VISIBLE);
                isUsernameError = true;
            } else {
                usernameError.setVisibility(View.GONE);
            }

            // Validate password
            if (password.isEmpty()) {
                passwordError.setText(getString(R.string.error_password_required));
                passwordError.setVisibility(View.VISIBLE);
                isPasswordError = true;
            } else if (!isValidPassword(password)) {
                passwordError.setText(getString(R.string.error_password_invalid));
                passwordError.setVisibility(View.VISIBLE);
                isPasswordError = true;
            } else {
                passwordError.setVisibility(View.GONE);
            }

            // Validate email
            if (email.isEmpty()) {
                emailError.setText(getString(R.string.error_email_required));
                emailError.setVisibility(View.VISIBLE);
                isEmailError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.setText(getString(R.string.error_email_invalid));
                emailError.setVisibility(View.VISIBLE);
                isEmailError = true;
            } else {
                emailError.setVisibility(View.GONE);
            }

            // If any validation failed, prevent further signup
            if (isUsernameError || isPasswordError || isEmailError) {
                return;
            }

            // Asynchronously check if the username is already taken
            userViewModel.getUserByUsername(username).observe(SignupActivity.this, user -> {
                if (user != null && !userCreationSuccess) {
                    // If the user already exists, show an error
                    usernameError.setText(getString(R.string.error_username_exists));
                    usernameError.setVisibility(View.VISIBLE);
                } else {
                    // If username is available, proceed with creating a new user
                    User newUser = new User();
                    newUser.username = username;
                    newUser.password = password;
                    newUser.email = email;

                    // Insert the new user into the database
                    userViewModel.insert(newUser);

                    // Mark user creation as successful
                    userCreationSuccess = true;

                    // Show success message and navigate to MainActivity
                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                }
            });
        });
    }

    /**
     * Checks if the password meets the required strength (at least one uppercase letter and one special character).
     * @param password The password to validate
     * @return true if password is valid, false otherwise
     */
    private boolean isValidPassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase()); // Check for at least one uppercase letter
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*"); // Check for at least one special character
        return hasUppercase && hasSpecial;
    }

    /**
     * Toggles password visibility (between visible and hidden).
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide the password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.notview); // Set the "eye closed" icon
        } else {
            // Show the password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.view); // Set the "eye open" icon
        }

        // Update the password visibility flag
        isPasswordVisible = !isPasswordVisible;
        passwordInput.setSelection(passwordInput.length()); // Keep the cursor at the end of the text
    }
}