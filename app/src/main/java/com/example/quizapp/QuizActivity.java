package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private TextView questionCounter, questionText;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button prevButton, submitButton;

    private List<Question> questionList = new ArrayList<>(); // Initialize to avoid null checks
    private int currentQuestionIndex = 0;
    private int[] selectedAnswers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();
        fetchQuestionsFromApi();

        setupButtonListeners();
    }

    // Initialize views
    private void initializeViews() {
        questionCounter = findViewById(R.id.question_counter);
        questionText = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        prevButton = findViewById(R.id.prev_button);
        submitButton = findViewById(R.id.submit_button);
    }

    // Fetch questions from the API
    private void fetchQuestionsFromApi() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<QuestionResponse> call = apiInterface.getQuestions();
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getQuestions() != null) {
                    questionList = response.body().getQuestions();

                    if (questionList.isEmpty()) {
                        showToastAndFinish("No questions available");
                        return;
                    }

                    initializeSelectedAnswers();
                    loadQuestion();
                } else {
                    showToastAndFinish("Failed to load questions");
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                showToastAndFinish("Error: " + t.getMessage());
            }
        });
    }

    // Initialize selected answers array
    private void initializeSelectedAnswers() {
        selectedAnswers = new int[questionList.size()];
        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1; // Indicates no answer selected
        }
    }

    // Setup button listeners
    private void setupButtonListeners() {
        prevButton.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                loadQuestion();
            }
        });

        submitButton.setOnClickListener(v -> {
            saveSelectedAnswer();
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                loadQuestion();
            } else {
                submitQuiz();
            }
        });
    }

    // Load the current question
    private void loadQuestion() {
        if (questionList.isEmpty()) {
            showToast("No questions available");
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);

        questionCounter.setText(String.format("Question: %d/%d", currentQuestionIndex + 1, questionList.size()));
        questionText.setText(Html.fromHtml(currentQuestion.getQuestionText(), Html.FROM_HTML_MODE_LEGACY));

        // Shuffle and display answer options
        List<String> answerOptions = new ArrayList<>(currentQuestion.getIncorrectAnswers());
        answerOptions.add(currentQuestion.getCorrectAnswer());
        Collections.shuffle(answerOptions);

        displayAnswerOptions(answerOptions);

        // Restore previously selected answer, if any
        optionsGroup.clearCheck();
        if (selectedAnswers[currentQuestionIndex] != -1) {
            ((RadioButton) optionsGroup.getChildAt(selectedAnswers[currentQuestionIndex])).setChecked(true);
        }
    }

    // Display answer options in the RadioButtons
    private void displayAnswerOptions(List<String> options) {
        RadioButton[] radioButtons = {option1, option2, option3, option4};
        for (int i = 0; i < options.size(); i++) {
            radioButtons[i].setText(Html.fromHtml(options.get(i), Html.FROM_HTML_MODE_LEGACY));
            radioButtons[i].setVisibility(View.VISIBLE); // Ensure visibility
        }

        // Hide any unused RadioButtons
        for (int i = options.size(); i < radioButtons.length; i++) {
            radioButtons[i].setVisibility(View.GONE);
        }
    }

    // Save the selected answer for the current question
    private void saveSelectedAnswer() {
        int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int selectedOptionIndex = optionsGroup.indexOfChild(findViewById(selectedOptionId));
            if (currentQuestionIndex >= 0 && currentQuestionIndex < selectedAnswers.length) {
                selectedAnswers[currentQuestionIndex] = selectedOptionIndex;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("An error occurred while saving the answer.");
        }
    }

    // Submit the quiz and calculate the score
    private void submitQuiz() {
        try {
            saveSelectedAnswer();

            int score = calculateScore();

            Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("DATE", getCurrentDate());
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("An error occurred while submitting the quiz.");
        }
    }

    // Calculate the quiz score
    private int calculateScore() {
        int score = 0;

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            if (i < selectedAnswers.length && selectedAnswers[i] != -1) {
                String selectedAnswerText = "";
                try {
                    selectedAnswerText = ((RadioButton) optionsGroup.getChildAt(selectedAnswers[i])).getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (selectedAnswerText.equals(Html.fromHtml(question.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString())) {
                    score++;
                }
            }
        }
        return score;
    }

    // Get the current date
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Utility to show a toast and finish the activity
    private void showToastAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    // Utility to show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
