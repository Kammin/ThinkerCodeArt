package com.example.kamin.thinkercodeart.rest;

import com.example.kamin.thinkercodeart.model.Idea;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;



public interface ApiInterface {
    @GET("ideas")
    Call<List<Idea>> getIdeas();


}

