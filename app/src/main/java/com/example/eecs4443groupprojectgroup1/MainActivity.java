package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheckbox;
    private MaterialButton loginButton;
    private TextView createAccountText;    // "Create account" TextView (blue and underlined)

    private UserViewModel userViewModel;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        rememberMeCheckbox = findViewById(R.id.remember_me_checkbox);
        loginButton = findViewById(R.id.login_btn);
        createAccountText = findViewById(R.id.create_account_link); // Create account TextView (blue and underlined)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Initialize ViewModel
        userViewModel = new UserViewModel(getApplication());

        // Check for auto-login using saved username in SharedPreferences
        String savedUsername = sharedPreferences.getString("username", null);
        if (savedUsername != null) {
            // If username exists, navigate directly to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("username", savedUsername);
            startActivity(intent);
            finish();  // Finish MainActivity so user can't go back
            return;    // Ensure that the rest of onCreate doesn't execute if auto-login happens
        }

        // Set underline for "Create Account" TextView
        createAccountText.setPaintFlags(createAccountText.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);

        // Handle login button click
        loginButton.setOnClickListener(v -> attemptLogin());

        // Handle "Create Account" click
        createAccountText.setOnClickListener(v -> navigateToCreateAccount());

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

    // Attempt to login
    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt login through ViewModel
        userViewModel.login(username, password).observe(this, user -> {
            if (user != null) {
                // Login successful
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                // Save the username if "Remember Me" is checked
                if (rememberMeCheckbox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();
                }
                // Navigate to HomeActivity
                navigateToHome();
            } else {
                // Login failed
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Navigate to HomeActivity
    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Navigate to CreateAccountActivity
    private void navigateToCreateAccount() {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
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