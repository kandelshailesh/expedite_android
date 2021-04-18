package com.example.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SubscriptionInterface {
    @Headers("Content-Type: application/json")
    @GET("api/backend/v1/subscriber")
    Call<ResponseBody> fetch(@Query("user_id") Integer  user_id);

    @Headers("Content-Type: application/json")
    @POST("api/backend/v1/subscriber")
    Call<ResponseBody> create(@Body String data);

    @Headers("Content-Type: application/json")
    @PATCH("api/backend/v1/subscriber/{id}")
    Call<ResponseBody> update(@Path ("id") Integer id,@Body String data);

    @Headers("Content-Type: application/json")
    @DELETE("api/backend/v1/subscriber/{id}")
    Call<ResponseBody> delete(@Path ("id") Integer id);

}
