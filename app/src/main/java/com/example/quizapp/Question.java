package com.example.quizapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question {

    @SerializedName("question")
    private String questionText;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    @SerializedName("correct_answer")
    private String correctAnswer;

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
