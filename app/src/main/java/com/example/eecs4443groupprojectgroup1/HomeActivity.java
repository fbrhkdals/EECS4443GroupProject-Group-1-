package com.example.eecs4443groupprojectgroup1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//just for test need to change
public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // Retrieve the username passed from the previous activity (MainActivity)
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        // If no username was passed, set the default as "User"
        if (username == null || username.isEmpty()) {
            username = "User";
        }

        // Set the welcome message on the TextView widget
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        welcomeMessage.setText("Welcome, " + username + "!");

        // Handle logout functionality
        Button logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear saved login preferences (username and "remember me" state)
                SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear(); // Clears all the shared preferences (or you can just remove specific keys like "username")
                editor.apply();

                // Navigate back to the login screen (MainActivity)
                Intent loginIntent = new Intent(HomeActivity.this, MainActivity.class);

                // Set flags to clear the activity stack and prevent navigating back to the home page
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the login screen and finish the current HomePageActivity
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
