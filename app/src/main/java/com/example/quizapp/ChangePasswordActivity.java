package com.example.quizapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtCurrentPassword, edtNewPassword;
    Button btnUpdatePassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        dbHelper = new DatabaseHelper(this);

        // Log all users for debugging
        dbHelper.logAllUsers();

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = edtCurrentPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String currentUsername = getCurrentUsername(); // Fetch the logged-in user's username

                Log.d("ChangePassword", "Username: " + currentUsername);
                Log.d("ChangePassword", "Current Password: " + currentPassword);
                Log.d("ChangePassword", "New Password: " + newPassword);

                if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentPassword.equals(newPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "New password must be different from the current password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.authenticateUser(currentUsername, currentPassword)) {
                    boolean isUpdated = dbHelper.updatePassword(currentUsername, newPassword);
                    if (isUpdated) {
                        Toast.makeText(ChangePasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        dbHelper.logAllUsers(); // Log users after the update
                        finish(); // Close the activity after successful update
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("username", ""); // Fetch the saved username
    }
}