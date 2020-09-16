package com.lattice.audioapp.services;

import com.lattice.audioapp.models.Results;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("uamp/catalog.json")
    Call<Results> getResults();
}
