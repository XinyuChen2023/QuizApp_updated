package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences sh;

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
        sh = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtAcc.getText().toString();
                String password = txtPassword.getText().toString();

                
                String savedUser = sh.getString("username", "");
                String savedPassword = sh.getString("password", "");

                if(userName.equals(savedUser) && password.equals(savedPassword)){
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Wrong user name or password, please try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("username", txtAcc.getText().toString());
                editor.putString("username", txtPassword.getText().toString());
                editor.apply();
                Toast.makeText(MainActivity.this, "Congregation! You have successfully create an account!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}