package com.example.quizapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private TextView questionCounter, questionText;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button prevButton, submitButton;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int[] selectedAnswers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionCounter = findViewById(R.id.question_counter);
        questionText = findViewById(R.id.question_text);
        optionsGroup = findViewById(R.id.options_group);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        prevButton = findViewById(R.id.prev_button);
        submitButton = findViewById(R.id.submit_button);


        fetchQuestionsFromApi();

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

    private void fetchQuestionsFromApi() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<QuestionResponse> call = apiInterface.getQuestions();
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body().getQuestions();

                    if (questionList == null || questionList.isEmpty()) {
                        Toast.makeText(QuizActivity.this, "No questions available", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }


                    selectedAnswers = new int[questionList.size()];
                    for (int i = 0; i < selectedAnswers.length; i++) {
                        selectedAnswers[i] = -1;
                    }

                    loadQuestion();
                } else {
                    Toast.makeText(QuizActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    private void loadQuestion() {
        if (questionList == null || questionList.isEmpty()) {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);


        questionCounter.setText("Question: " + (currentQuestionIndex + 1) + "/" + questionList.size());
        questionText.setText(currentQuestion.getQuestionText());


        List<String> answerOptions = new ArrayList<>(currentQuestion.getIncorrectAnswers());
        answerOptions.add(currentQuestion.getCorrectAnswer());
        Collections.shuffle(answerOptions);

        option1.setText(answerOptions.get(0));
        option2.setText(answerOptions.get(1));
        option3.setText(answerOptions.get(2));
        option4.setText(answerOptions.get(3));


        optionsGroup.clearCheck();
        if (selectedAnswers[currentQuestionIndex] != -1) {
            ((RadioButton) optionsGroup.getChildAt(selectedAnswers[currentQuestionIndex])).setChecked(true);
        }
    }


    private void saveSelectedAnswer() {
        int selectedOptionId = optionsGroup.getCheckedRadioButtonId();
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedOptionIndex = optionsGroup.indexOfChild(findViewById(selectedOptionId));
        selectedAnswers[currentQuestionIndex] = selectedOptionIndex;
    }

    private void submitQuiz() {
        int score = calculateScore();
        Toast.makeText(this, "Quiz completed! Your score: " + score, Toast.LENGTH_LONG).show();
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            int selectedAnswerIndex = selectedAnswers[i];

            if (selectedAnswerIndex != -1) {

                String selectedAnswerText = ((RadioButton) optionsGroup.getChildAt(selectedAnswerIndex)).getText().toString();


                if (selectedAnswerText.equals(question.getCorrectAnswer())) {
                    score++;
                }
            }
        }
        return score;
    }

}
