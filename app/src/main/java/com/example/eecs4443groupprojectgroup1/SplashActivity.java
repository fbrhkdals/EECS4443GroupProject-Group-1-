package com.example.eecs4443groupprojectgroup1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 레이아웃을 activity_splash로 설정

        // Load img
        ImageView logoImageView = findViewById(R.id.logo_image); // activity_splash.xml에서 ImageView의 ID 설정

        // Move to MainActivity after 2sec
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // SplashActivity End
        }, 2000); // 2000ms (2초)
    }
}
