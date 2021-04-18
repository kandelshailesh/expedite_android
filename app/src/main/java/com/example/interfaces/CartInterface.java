package com.example.interfaces;

import android.view.View;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public  interface CartInterface {

    void addtoCart(View v, Integer position, Integer quantity, Double price);
}
