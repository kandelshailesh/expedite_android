package com.example.interfaces;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface OrderInterface {
    @Headers("Content-Type: application/json")
    @GET("api/backend/v1/orders")
    Call<ResponseBody> fetch(@Query("user_id") Integer  user_id);

    @Headers("Content-Type: application/json")
    @POST("api/backend/v1/orders")
    Call<ResponseBody> create(@Body String data);

}
