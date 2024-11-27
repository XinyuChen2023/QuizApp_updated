package com.example.quizapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuestionResponse {
    @SerializedName("results")
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}

