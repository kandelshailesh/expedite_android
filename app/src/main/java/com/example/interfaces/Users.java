package com.example.interfaces;

import android.widget.RadioButton;

import com.example.models.users.Login;
import com.example.models.users.Register;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Users {
    @Headers("Content-Type: application/json")
    @POST("api/backend/v1/user/login")
    Call<ResponseBody> login(@Body String user);

    @FormUrlEncoded
    @POST("api/backend/v1/users")
    Call<ResponseBody> signup(@Field("fullName") String fullName, @Field("username") String username, @Field("email") String email, @Field("password") String password, @Field("phone") String phone, @Field("address") String address, @Field("gender") String gender);
}
