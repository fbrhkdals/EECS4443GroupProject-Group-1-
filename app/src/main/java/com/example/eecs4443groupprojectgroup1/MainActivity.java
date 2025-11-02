package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheckbox;
    private MaterialButton loginButton;
    private TextView createAccountText;
    private ImageView passwordToggleIcon;

    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;

    // Track password visibility state
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_btn);
        createAccountText = findViewById(R.id.create_account_link);
        passwordToggleIcon = findViewById(R.id.password_toggle_icon);  // new icon view

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Initialize ViewModel
        userViewModel = new UserViewModel(getApplication());

        // Check for auto-login using saved preferences
        String savedUsername = sharedPreferences.getString("username", null); // Always saved
        boolean autoLogin = sharedPreferences.getBoolean("autoLogin", false); // Checkbox based

        if (savedUsername != null && autoLogin) {
            // If auto-login is enabled, navigate directly to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("username", savedUsername);
            startActivity(intent);
            finish(); // Prevent back navigation to login
            return; // Ensure rest of onCreate is not executed
        }

        // Underline "Create Account"
        createAccountText.setPaintFlags(createAccountText.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        // Login button click
        loginButton.setOnClickListener(v -> attemptLogin());

        // Create account link click
        createAccountText.setOnClickListener(v -> navigateToCreateAccount());

        // Password visibility toggle icon click
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
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                // Save username and auto-login preference
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Always save username (for later use in SettingsFragment)
                editor.putString("username", username);

                // Save auto-login preference if "Remember Me" is checked
                editor.putBoolean("autoLogin", rememberMeCheckbox.isChecked());

                editor.apply();

                // Navigate to HomeActivity
                navigateToHome(username);
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to home
    private void navigateToHome(String username) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Finish MainActivity so user can't go back
    }

    // Navigate to create account
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

        // Move cursor to the end
        passwordInput.setSelection(passwordInput.length());
    }
}