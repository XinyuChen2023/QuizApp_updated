package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);


        TextView dateTextView = findViewById(R.id.current_date);
        TextView userScoreTextView = findViewById(R.id.user_score);
        Button tryNewQuizButton = findViewById(R.id.try_new_quiz_button);
        Button viewAccountButton = findViewById(R.id.view_account_button);

        int score = getIntent().getIntExtra("SCORE", 0);
        String date = getIntent().getStringExtra("DATE");

        // Display results
        userScoreTextView.setText(score + "/10");
        dateTextView.setText("Date: " + date);

        // Set a click listener for the "Try a New Quiz" button
        tryNewQuizButton.setOnClickListener(v -> {
            // Navigate back to the main quiz screen
            Intent intent = new Intent(QuizResultActivity.this, QuizActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the current stack
            startActivity(intent);
            finish();
        });

        // Set a click listener for the "View Account" button

        viewAccountButton.setOnClickListener(v -> {
            // Navigate to the account screen (assume AccountActivity exists)
            Intent intent = new Intent(QuizResultActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });


    }
}
