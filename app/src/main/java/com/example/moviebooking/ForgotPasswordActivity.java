package com.example.moviebooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.moviebooking.data.FireBaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Views for verification
    private EditText usernameEditText;
    private EditText nameEditText;
    private Button verifyButton;

    // Views for reset password
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button resetPasswordButton;

    // Common views
    private Button backToLoginButton;
    private LinearLayout verificationSection;
    private LinearLayout resetPasswordSection;
    private TextView titleTextView;
    private TextView descriptionTextView;

    // User data
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize verification views
        usernameEditText = findViewById(R.id.usernameEditText);
        nameEditText = findViewById(R.id.nameEditText);
        verifyButton = findViewById(R.id.verifyButton);

        // Initialize reset password views
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Initialize common views
        backToLoginButton = findViewById(R.id.backToLoginButton);
        verificationSection = findViewById(R.id.verificationSection);
        resetPasswordSection = findViewById(R.id.resetPasswordSection);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        // Set click listeners
        verifyButton.setOnClickListener(v -> verifyUser());
        resetPasswordButton.setOnClickListener(v -> resetPassword());
        backToLoginButton.setOnClickListener(v -> finish());
    }

    private void verifyUser() {
        username = usernameEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify user with Firebase
        FireBaseManager.verifyUserForPasswordReset(this, username, name, new FireBaseManager.RegistrationCallback() {
            @Override
            public void onRegistrationResult(boolean isSuccess, String message, Object data) {
                if (isSuccess) {
                    // Show reset password section
                    switchToResetPasswordSection();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void switchToResetPasswordSection() {
        // Change UI to reset password mode
        titleTextView.setText("Reset Password");
        descriptionTextView.setText("Please enter your new password");

        // Hide verification section and show reset password section
        verificationSection.setVisibility(View.GONE);
        resetPasswordSection.setVisibility(View.VISIBLE);
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset password with Firebase
        FireBaseManager.resetPassword(this, username, newPassword, confirmPassword, new FireBaseManager.RegistrationCallback() {
            @Override
            public void onRegistrationResult(boolean isSuccess, String message, Object data) {
                if (isSuccess) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    // Navigate back to login screen
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}