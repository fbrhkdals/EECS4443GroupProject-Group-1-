package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Initialize UI components
        backButton = findViewById(R.id.back_button);

        // Back button: navigate to HomeActivity and finish current activity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}