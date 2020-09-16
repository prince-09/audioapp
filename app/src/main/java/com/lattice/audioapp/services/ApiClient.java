package com.lattice.audioapp.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://storage.googleapis.com/";
    public static ApiClient instance;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private Retrofit retrofit;
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    private ApiClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized ApiClient getInstance() {

        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}
