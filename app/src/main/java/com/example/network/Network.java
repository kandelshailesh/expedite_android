package com.example.network;

import android.os.Build;

import com.example.basic.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Network {
    public Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(BuildConfig.API_URL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public Retrofit getRetrofit1() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(BuildConfig.API_URL).
                addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }
}
