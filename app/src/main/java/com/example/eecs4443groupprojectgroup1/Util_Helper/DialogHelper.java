package com.example.eecs4443groupprojectgroup1.Util_Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.example.eecs4443groupprojectgroup1.R;
import com.example.eecs4443groupprojectgroup1.User.UserViewModel;

public class DialogHelper {

    // Method to show the email edit dialog
    public static void showEmailEditDialog(Context context, int currentUserId, UserViewModel userViewModel) {
        // Create the custom dialog for email
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.email_dialog, null);

        final EditText emailEditText = dialogView.findViewById(R.id.email_edit_text);
        emailEditText.setHint("Enter your Email");

        // Find the error TextView and set initial visibility to GONE
        final TextView emailErrorTextView = dialogView.findViewById(R.id.email_error);
        emailErrorTextView.setVisibility(View.GONE);

        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Save button logic
        Button saveButton = dialogView.findViewById(R.id.save_email_button);
        saveButton.setOnClickListener(v -> {
            String newEmail = emailEditText.getText().toString();

            // Clear any previous error message and hide error TextView
            emailErrorTextView.setText("");
            emailErrorTextView.setVisibility(View.GONE);

            // Validate email format
            if (!newEmail.isEmpty() && isValidEmail(newEmail)) {
                userViewModel.updateEmail(currentUserId, newEmail);  // Update email in the ViewModel
                Toast.makeText(context, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();  // Close the dialog after updating the email
            } else {
                // Set error message and make the error TextView visible
                emailErrorTextView.setText("Invalid email format");
                emailErrorTextView.setVisibility(View.VISIBLE);
            }
        });

        // Cancel button logic
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            // Close the dialog without doing anything
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    // Method to show the password change dialog
    public static void showPasswordChangeDialog(Context context, int currentUserId, UserViewModel userViewModel) {
        // Create the custom dialog for password change
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.password_dialog, null);

        final EditText newPasswordEditText = dialogView.findViewById(R.id.password_edit_text);  // New password
        final EditText confirmPasswordEditText = dialogView.findViewById(R.id.password_check_text);  // Confirm password

        // Find the error TextView and set initial visibility to GONE
        final TextView passwordErrorTextView = dialogView.findViewById(R.id.password_error);
        passwordErrorTextView.setVisibility(View.GONE);

        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Save button logic
        Button saveButton = dialogView.findViewById(R.id.save_password_button);
        saveButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Clear any previous error message and hide error TextView
            passwordErrorTextView.setText("");
            passwordErrorTextView.setVisibility(View.GONE);

            // First, validate the new password format
            if (!isValidPassword(newPassword)) {
                passwordErrorTextView.setText("Password must contain at least one uppercase letter and one special character");
                passwordErrorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // Then, check if the new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                passwordErrorTextView.setText("Passwords do not match");
                passwordErrorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // If both checks pass, update the password
            userViewModel.updatePassword(currentUserId, newPassword);  // Update password in the ViewModel
            Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();  // Close the dialog after updating the password
        });

        // Cancel button logic
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            // Close the dialog without doing anything
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    // Method to show the Date of Birth edit dialog
    public static void showDateOfBirthDialog(Context context, int currentUserId, UserViewModel userViewModel) {
        // Create the custom dialog for date of birth
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dateofbirth_dialog, null);

        // EditText for Day, Month, and Year
        final EditText dayEditText = dialogView.findViewById(R.id.day_input);
        final EditText monthEditText = dialogView.findViewById(R.id.month_input);
        final EditText yearEditText = dialogView.findViewById(R.id.year_input);

        // Set hints for Day, Month, and Year
        dayEditText.setHint("Day");
        monthEditText.setHint("Month");
        yearEditText.setHint("Year");

        // Error TextView
        final TextView dateOfBirthErrorTextView = dialogView.findViewById(R.id.dateOfBirth_error);
        dateOfBirthErrorTextView.setVisibility(View.GONE);  // Initially hide the error message

        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Save button logic
        Button saveButton = dialogView.findViewById(R.id.save_dateOfBirth_button);
        saveButton.setOnClickListener(v -> {
            // Get the input values for day, month, and year
            String day = dayEditText.getText().toString();
            String month = monthEditText.getText().toString();
            String year = yearEditText.getText().toString();

            // Reset error message visibility
            dateOfBirthErrorTextView.setVisibility(View.GONE);

            // Format day and month to two digits if they are one digit
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (month.length() == 1) {
                month = "0" + month;
            }

            // Validate that the fields are not empty
            if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                dateOfBirthErrorTextView.setText("Please enter a valid date.");
                dateOfBirthErrorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // Validate the date format (e.g., checking if the date is valid like 29th Feb)
            if (!isValidDate(day, month, year)) {
                dateOfBirthErrorTextView.setText("Invalid date.");
                dateOfBirthErrorTextView.setVisibility(View.VISIBLE);
                return;
            }

            // If date is valid, update the date of birth
            String formattedDate = year + " - " + month + " - " + day;
            userViewModel.updateDateOfBirth(currentUserId, formattedDate);

            // Notify success
            Toast.makeText(context, "Date of birth updated successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();  // Close the dialog
        });

        // Cancel button logic
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            // Close the dialog without doing anything
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    // Method to show the Gender edit dialog
    public static void showGenderEditDialog(Context context, int currentUserId, UserViewModel userViewModel) {
        // Create the custom dialog for gender
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.gender_dialog, null);

        // RadioGroup for selecting gender
        RadioGroup genderRadioGroup = dialogView.findViewById(R.id.gender_radio_group);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // **Handling LiveData asynchronously**: Observing user data asynchronously
        userViewModel.getUserById(currentUserId).observe((LifecycleOwner) context, user -> {
            if (user != null && user.gender != null) {
                // Set the selected RadioButton based on the user's gender
                if ("Male".equals(user.gender)) {
                    genderRadioGroup.check(R.id.radio_male);
                } else if ("Female".equals(user.gender)) {
                    genderRadioGroup.check(R.id.radio_female);
                } else if ("Unspecified".equals(user.gender)) {
                    genderRadioGroup.check(R.id.radio_unspecified);
                }
            }
        });

        // Save button logic
        Button saveButton = dialogView.findViewById(R.id.save_gender_button);
        saveButton.setOnClickListener(v -> {
            // Get the selected gender from the RadioGroup
            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = dialogView.findViewById(selectedId);
            String selectedGender = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "Unspecified";

            // Update the user's gender in the ViewModel
            userViewModel.updateGender(currentUserId, selectedGender);

            // Notify the user
            Toast.makeText(context, "Gender updated successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();  // Close the dialog after updating the gender
        });

        // Cancel button logic
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();  // Close the dialog without doing anything
        });

        // Show the dialog
        dialog.setView(dialogView); // Set the custom layout
        dialog.show();
    }

    // Method to show the Description edit dialog with 100 characters and 4 lines restriction
    public static void showDescriptionEditDialog(Context context, int currentUserId, UserViewModel userViewModel) {
        // Create a builder for the dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the custom layout for the dialog from XML
        View dialogView = LayoutInflater.from(context).inflate(R.layout.description_dialog, null);

        // Get the EditText where the user can type their description
        EditText descriptionEditText = dialogView.findViewById(R.id.description_edit_text);

        // Get the TextView where error message will be displayed
        TextView descriptionErrorTextView = dialogView.findViewById(R.id.description_error);

        // Clear any previous error message
        descriptionErrorTextView.setVisibility(View.GONE);

        // Create the AlertDialog using the builder
        AlertDialog dialog = builder.create();

        // **Handling LiveData asynchronously**: Observing user data asynchronously
        // Get the current description from the ViewModel and populate the EditText field
        userViewModel.getUserById(currentUserId).observe((LifecycleOwner) context, user -> {
            if (user != null && user.description != null) {
                // Set the current description as the text in the EditText, but we'll hide it initially
                descriptionEditText.setText(""); // Clear the existing text so only the hint is visible
            }
        });

        // Set a hint for the EditText to indicate what the user should type
        descriptionEditText.setHint("Type your description...");

        // Save button logic
        // When the user clicks the "Save" button, update the description in the ViewModel
        Button saveButton = dialogView.findViewById(R.id.save_description_button);
        saveButton.setOnClickListener(v -> {
            // Get the text entered by the user in the EditText
            String updatedDescription = descriptionEditText.getText().toString();

            // Clear any previous error message
            descriptionErrorTextView.setVisibility(View.GONE);

            // Check if the input exceeds 100 characters
            if (updatedDescription.length() > 100) {
                // Show error in TextView if more than 100 characters
                descriptionErrorTextView.setVisibility(View.VISIBLE);
                descriptionErrorTextView.setText("Description can't exceed 100 characters.");
                return;
            }

            // Check if the input exceeds 4 lines using getLineCount() method
            if (descriptionEditText.getLineCount() > 4) {
                // Show error in TextView if more than 4 lines
                descriptionErrorTextView.setVisibility(View.VISIBLE);
                descriptionErrorTextView.setText("Description can't exceed 4 lines.");
                return;
            }

            // **Allow empty description**
            // Check if input is empty
            if (updatedDescription.trim().isEmpty()) {
                // Show confirmation dialog for empty input
                new AlertDialog.Builder(context)
                        .setTitle("Empty Description")
                        .setMessage("Are you sure you want to leave the description empty?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            // Save empty description
                            userViewModel.updateDescription(currentUserId, null);
                            descriptionErrorTextView.setVisibility(View.GONE);
                            dialog.dismiss(); // Close the main dialog
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            // Do nothing, let user edit again
                            dialogInterface.dismiss();
                        })
                        .show();
                return;
            }

            // If valid (even if empty), update the description in the ViewModel
            userViewModel.updateDescription(currentUserId, updatedDescription);

            // Notify the user that the description has been updated (or removed if empty)
            descriptionErrorTextView.setVisibility(View.GONE); // Hide error message if valid
            Toast.makeText(context, "Description updated successfully!", Toast.LENGTH_SHORT).show();

            // Dismiss the dialog after the update
            dialog.dismiss();
        });

        // Cancel button logic
        // When the user clicks the "Cancel" button, dismiss the dialog without making any changes
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            // Close the dialog without saving any changes
            dialog.dismiss();
        });

        // Set the custom layout for the dialog and show it
        dialog.setView(dialogView);
        dialog.show();
    }

    // Utility method to validate email format
    private static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Utility method to validate password format (at least one uppercase letter and one special character)
    private static boolean isValidPassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUppercase && hasSpecial;
    }

    // Utility method to validate the date (check if it's a valid date like 29th Feb)
    private static boolean isValidDate(String dayStr, String monthStr, String yearStr) {
        try {
            // Convert input strings to integers
            int day = Integer.parseInt(dayStr);
            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);

            // Check if the year is valid (between 1 and 9999)
            if (year < 1 || year > 9999) {
                return false;
            }

            // Check if the month is valid (1 to 12)
            if (month < 1 || month > 12) {
                return false;
            }

            // Check if the day is valid for the given month and year
            if (!isValidDayForMonth(day, month, year)) {
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            // If input is not a valid integer (invalid date format)
            return false;
        }
    }

    // Method to check if the day is valid for the given month and year
    private static boolean isValidDayForMonth(int day, int month, int year) {
        // Days in each month for a normal (non-leap) year
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // Adjust for leap year if month is February
        if (month == 2 && isLeapYear(year)) {
            daysInMonth[1] = 29; // February has 29 days in a leap year
        }

        // Check if the day is valid for the given month
        return day >= 1 && day <= daysInMonth[month - 1];
    }

    // Method to check if the given year is a leap year
    private static boolean isLeapYear(int year) {
        // Leap year rules:
        // 1. Year is divisible by 4
        // 2. Year is not divisible by 100, unless it's also divisible by 400
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }
}