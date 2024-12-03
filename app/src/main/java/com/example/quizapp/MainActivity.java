package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText txtAcc, txtPassword;
    Button btnLogin, btnCreate;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtAcc = findViewById(R.id.txtAcc);
        txtPassword = findViewById(R.id.txtPassword);
        dbHelper = new DatabaseHelper(this);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtAcc.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dbHelper.authenticateUser(userName, password)) {
                    // Save the username in SharedPreferences for future use
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("username", userName)
                            .apply();

                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Wrong username or password, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtAcc.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isAdded = dbHelper.addUser(userName, password);
                if (isAdded) {
                    Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Account creation failed. Username may already exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
