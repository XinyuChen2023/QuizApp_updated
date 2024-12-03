package com.example.quizapp;

import android.content.Intent;
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

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = edtCurrentPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String currentUsername = getCurrentUsername();

                Log.d("ChangePassword", "Username: " + currentUsername);
                Log.d("ChangePassword", "Entered Current Password: " + currentPassword);

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

                        // Redirect to MainActivity after successful password change
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the back stack
                        startActivity(intent);
                        finish(); // Close current activity
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
        String username = sharedPreferences.getString("username", "");
        Log.d("ChangePassword", "Fetched Username: " + username);
        return username;
    }
}
