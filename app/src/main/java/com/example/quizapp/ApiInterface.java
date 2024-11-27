package com.example.quizapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api.php?amount=10&category=18")
    Call<QuestionResponse> getQuestions();
}
