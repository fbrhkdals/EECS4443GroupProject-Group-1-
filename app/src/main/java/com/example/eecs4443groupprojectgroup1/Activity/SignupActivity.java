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

    private EditText usernameInput, passwordInput, emailInput;
    private MaterialButton signupBtn;
    private UserViewModel userViewModel;

    private TextView usernameError, passwordError, emailError;
    private ImageView passwordToggleIcon;

    private boolean isPasswordVisible = false;
    private boolean userCreationSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        signupBtn = findViewById(R.id.signup_btn);
        passwordToggleIcon = findViewById(R.id.password_toggle_icon);

        // Initialize TextViews for error messages
        usernameError = findViewById(R.id.username_error);
        passwordError = findViewById(R.id.password_error);
        emailError = findViewById(R.id.email_error);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(UserViewModel.class);

        // Back button: navigate to MainActivity and finish
        findViewById(R.id.back_button).setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        });

        // Password visibility toggle icon click
        passwordToggleIcon.setOnClickListener(v -> togglePasswordVisibility());

        // Signup button logic
        signupBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

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

            // If any field has error, do not proceed
            if (isUsernameError || isPasswordError || isEmailError) {
                return;
            }

            // Check if user exists asynchronously
            userViewModel.getUserByUsername(username).observe(SignupActivity.this, user -> {
                if (user != null && !userCreationSuccess) {
                    // Username already exists
                    usernameError.setText(getString(R.string.error_username_exists));
                    usernameError.setVisibility(View.VISIBLE);
                } else {
                    // Username is unique, proceed with signup
                    User newUser = new User();
                    newUser.username = username;
                    newUser.password = password;
                    newUser.email = email;

                    userViewModel.insert(newUser);

                    // Mark signup success
                    userCreationSuccess = true;

                    Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                }
            });
        });
    }

    /**
     * Checks if password is valid (at least one uppercase and one special character)
     */
    private boolean isValidPassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUppercase && hasSpecial;
    }

    /**
     * Toggles password visibility
     */
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.notview);
        } else {
            // Show password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggleIcon.setImageResource(R.drawable.view);
        }

        isPasswordVisible = !isPasswordVisible;
        passwordInput.setSelection(passwordInput.length());
    }
}