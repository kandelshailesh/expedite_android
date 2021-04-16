package com.example.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CategoryInterface {
    @Headers("Content-Type: application/json")
    @GET("api/backend/v1/category")
    Call<ResponseBody> fetch(@Query("product") String  status);
}
