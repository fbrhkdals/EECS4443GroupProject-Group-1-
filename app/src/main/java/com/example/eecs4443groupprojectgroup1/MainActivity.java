package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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

    private LoginViewModel loginViewModel;
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
        loginViewModel = new LoginViewModel(getApplication());

        // Auto-login
        String savedUsername = sharedPreferences.getString("username", null);
        if (savedUsername != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("username", savedUsername);
            startActivity(intent);
            finish();
            return;
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

        loginViewModel.login(username, password).observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                if (rememberMeCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();
                }

                navigateToHome();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to home
    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Navigate to create account
    private void navigateToCreateAccount() {
        Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
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