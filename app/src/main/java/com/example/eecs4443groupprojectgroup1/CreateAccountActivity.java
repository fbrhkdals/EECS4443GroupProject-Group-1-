package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, emailInput;
    private MaterialButton signupBtn;
    private UserViewModel userViewModel;

    // Declare TextViews for error messages above the EditTexts
    private TextView usernameError, passwordError, emailError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        // Initialize UI components
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        signupBtn = findViewById(R.id.signup_btn);
        ImageButton backButton = findViewById(R.id.back_button);

        // Initialize TextViews for error messages
        usernameError = findViewById(R.id.username_error);
        passwordError = findViewById(R.id.password_error);
        emailError = findViewById(R.id.email_error);

        // Initialize ViewModel for database operations
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(UserViewModel.class);

        // Back button: navigate to MainActivity and finish current activity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        signupBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            // Initialize error flags for each field
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

            // If any field has an error, return early
            if (isUsernameError || isPasswordError || isEmailError) {
                return;
            }

            // Check if username exists
            userViewModel.getUserByUsername(username).observe(CreateAccountActivity.this, user -> {
                if (user != null) {
                    // Username exists — show error on username field
                    usernameError.setText(getString(R.string.error_username_exists));
                    usernameError.setVisibility(View.VISIBLE);
                } else {
                    // Create new user and insert into database
                    User newUser = new User();
                    newUser.username = username;
                    newUser.password = password;
                    newUser.email = email;

                    userViewModel.insert(newUser);

                    // Show toast only on successful signup
                    Toast.makeText(CreateAccountActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to MainActivity
                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        });

        // Handle password visibility toggle on drawable end click
        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2; // right drawable index
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Check if user clicked on the right drawable (eye icon)
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Validates the password.
     * Checks if it contains at least one uppercase letter and one special character.
     *
     * @param password The password string to validate.
     * @return true if valid, false otherwise.
     */
    private boolean isValidPassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUppercase && hasSpecial;
    }

    // Toggle password visibility
    private void togglePasswordVisibility() {
        int inputType = passwordInput.getInputType();
        if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Show password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // Change the drawable icon to "view"
            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0);
        } else {
            // Hide password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // Change the drawable icon to "notview"
            passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.notview, 0);
        }

        // Maintain the cursor position at the end of the text
        passwordInput.setSelection(passwordInput.length());
    }
}