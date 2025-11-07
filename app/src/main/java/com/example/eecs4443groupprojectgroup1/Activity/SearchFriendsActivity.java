package com.example.eecs4443groupprojectgroup1.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;

public class SearchFriendsActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // searchfriends.xml
        setContentView(R.layout.searchfriends);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            SharedPreferencesHelper.saveCurrentTab(SearchFriendsActivity.this, "FRIENDS");
            startActivity(new Intent(SearchFriendsActivity.this, HomeActivity.class));
            finish();
        });
    }
}