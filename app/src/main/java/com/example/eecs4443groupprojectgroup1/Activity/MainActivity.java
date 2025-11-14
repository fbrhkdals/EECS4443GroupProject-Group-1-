package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private CheckBox rememberMeCheckbox;
    private MaterialButton loginButton;
    private TextView createAccountText;
    private ImageView passwordToggleIcon;

    private UserViewModel userViewModel;
    private boolean isPasswordVisible = false; // Track password visibility state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Initialize ViewModel
        userViewModel = new UserViewModel(getApplication());

        // Check for auto-login using saved preferences
        handleAutoLogin();

        // Set underline for "Create Account" link
        underlineCreateAccountLink();

        // Set up listeners
        setListeners();
    }

    // Initialize all views
    private void initializeViews() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_btn);
        createAccountText = findViewById(R.id.create_account_link);
        passwordToggleIcon = findViewById(R.id.password_toggle_icon);
    }

    // Handle auto-login based on saved preferences
    private void handleAutoLogin() {
        int savedUserId = SharedPreferencesHelper.getUserId(this);
        boolean autoLogin = SharedPreferencesHelper.getAutoLogin(this);

        if (savedUserId != -1 && autoLogin) {
            // Auto-login enabled, proceed to HomeActivity
            navigateToHome(savedUserId);
        }
    }

    // Underline the "Create Account" text
    private void underlineCreateAccountLink() {
        createAccountText.setPaintFlags(createAccountText.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
    }

    // Set listeners for buttons and actions
    private void setListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        createAccountText.setOnClickListener(v -> navigateToCreateAccount());
        passwordToggleIcon.setOnClickListener(v -> togglePasswordVisibility());
    }

    // Attempt login
    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt login through ViewModel
        userViewModel.login(username, password).observe(this, user -> {
            if (user != null) {
                handleSuccessfulLogin(user);
            } else {
                Toast.makeText(this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle successful login
    private void handleSuccessfulLogin(User user) {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

        // Save user ID and auto-login preference
        SharedPreferencesHelper.saveUserId(this, user.id);
        SharedPreferencesHelper.saveAutoLogin(this, rememberMeCheckbox.isChecked());

        // Navigate to HomeActivity
        navigateToHome(user.id);
    }

    // Navigate to HomeActivity
    private void navigateToHome(int userId) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish(); // Finish MainActivity to prevent back navigation
    }

    // Navigate to Create Account screen
    private void navigateToCreateAccount() {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    // Toggle password visibility
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

        // Move cursor to the end after changing the input type
        passwordInput.setSelection(passwordInput.length());
    }
}