package com.example.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DoctorInterface {
    @Headers("Content-Type: application/json")
    @GET("api/backend/v1/doctors")
    Call<ResponseBody> fetch();
}
