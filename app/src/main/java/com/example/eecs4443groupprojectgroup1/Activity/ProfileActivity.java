package com.example.eecs4443groupprojectgroup1.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.eecs4443groupprojectgroup1.User.User;
import com.example.eecs4443groupprojectgroup1.Util_Helper.DialogHelper;
import com.example.eecs4443groupprojectgroup1.Util_Helper.ImageUtil;
import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.Util_Helper.SharedPreferencesHelper;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 100;
    private static final int PERMISSION_REQUEST = 200;

    private UserViewModel userViewModel;
    private int currentUserId;

    private ImageButton backButton;
    private ImageView userIcon;
    private TextView userName, userDescription, userEmail, userDateOfBirth, userGender;

    private LinearLayout descriptionLayout, emailLayout, dateOfBirthLayout, genderLayout, passwordLayout;
    private FrameLayout userIconLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Retrieve currentUserId using SharedPreferencesHelper
        currentUserId = SharedPreferencesHelper.getUserId(this);
        if (currentUserId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish(); // Exit if the user is not found
            return;
        }

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Bind UI components
        initializeUI();

        // Load user data
        loadUserData();

        // Set up listeners for updating user data
        setUpClickListeners();
    }

    // Initialize UI components
    private void initializeUI() {
        backButton = findViewById(R.id.back_button);
        userIcon = findViewById(R.id.user_icon);
        userName = findViewById(R.id.user_name);
        userDescription = findViewById(R.id.user_description);
        userEmail = findViewById(R.id.user_email);
        userDateOfBirth = findViewById(R.id.user_dateOfBirth);
        userGender = findViewById(R.id.user_gender);

        descriptionLayout = findViewById(R.id.updateUser_description);
        emailLayout = findViewById(R.id.updateUser_email);
        dateOfBirthLayout = findViewById(R.id.updateUser_dateOfBirth);
        genderLayout = findViewById(R.id.updateUser_gender);
        passwordLayout = findViewById(R.id.updateUser_password);
        userIconLayout = findViewById(R.id.updateUser_icon);

        // Back button to return to HomeActivity
        backButton.setOnClickListener(v -> {
            SharedPreferencesHelper.saveCurrentTab(this, "SETTINGS");
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    // Load user data from ViewModel
    private void loadUserData() {
        userViewModel.getUserById(currentUserId).observe(this, user -> {
            if (user != null) {
                updateUIWithUserData(user);
            } else {
                setDefaultUserData();
            }
        });
    }

    // Update UI with user data
    private void updateUIWithUserData(User user) {
        userName.setText(user.username != null ? user.username : "Name");
        userDescription.setText(user.description != null ? user.description : "User has no description.");
        userEmail.setText(user.email != null ? user.email : "No email provided");
        userDateOfBirth.setText(user.dateOfBirth != null ? user.dateOfBirth : "Birthday not set");
        userGender.setText(user.gender != null ? user.gender : "Unspecified");

        // Handle profile image
        if (user.userIcon != null) {
            Bitmap bitmap = ImageUtil.decodeFromBase64(user.userIcon);
            if (bitmap != null) {
                userIcon.setImageBitmap(bitmap);
            }
        } else {
            userIcon.setImageResource(R.drawable.user_icon);
        }
    }

    // Set default data if user is not found in the database
    private void setDefaultUserData() {
        userName.setText("Name");
        userDescription.setText("User has no description.");
        userEmail.setText("No email provided");
        userDateOfBirth.setText("Birthday not set");
        userGender.setText("Unspecified");
        userIcon.setImageResource(R.drawable.user_icon);
    }

    // Set up listeners for updating user data
    private void setUpClickListeners() {
        descriptionLayout.setOnClickListener(v -> DialogHelper.showDescriptionEditDialog(this, currentUserId, userViewModel));
        emailLayout.setOnClickListener(v -> DialogHelper.showEmailEditDialog(this, currentUserId, userViewModel));
        dateOfBirthLayout.setOnClickListener(v -> DialogHelper.showDateOfBirthDialog(this, currentUserId, userViewModel));
        genderLayout.setOnClickListener(v -> DialogHelper.showGenderEditDialog(this, currentUserId, userViewModel));
        passwordLayout.setOnClickListener(v -> DialogHelper.showPasswordChangeDialog(this, currentUserId, userViewModel));

        // Handle profile image update
        userIconLayout.setOnClickListener(v -> showImageOptionsDialog());
    }

    // Show dialog for selecting profile image options
    private void showImageOptionsDialog() {
        String[] options = {"Default Image", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Image");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Remove image
                    userViewModel.updateUserIconById(currentUserId, null);
                    userIcon.setImageResource(R.drawable.user_icon);
                    break;
                case 1: // Open gallery to pick an image
                    checkPermissionAndOpenGallery();
                    break;
            }
        });
        builder.show();
    }

    // Check permissions and open gallery if permission is granted
    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST);
            } else {
                openGallery();
            }
        } else { // Below Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                openGallery();
            }
        }
    }

    // Open gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Handle the image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    String base64String = ImageUtil.encodeToBase64(bitmap, 432, 432, 80);

                    // Update ImageView
                    Bitmap resizedBitmap = ImageUtil.decodeFromBase64(base64String);
                    if (resizedBitmap != null) userIcon.setImageBitmap(resizedBitmap);

                    // Save to DB
                    userViewModel.updateUserIconById(currentUserId, base64String);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}