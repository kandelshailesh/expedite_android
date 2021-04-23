package com.example.interfaces;

import android.widget.RadioButton;

import com.example.models.users.Login;
import com.example.models.users.Register;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface Users {
    @Headers("Content-Type: application/json")
    @POST("api/backend/v1/user/login")
    Call<ResponseBody> login(@Body String user);

    @FormUrlEncoded
    @POST("api/backend/v1/users")
    Call<ResponseBody> signup(@Field("fullName") String fullName, @Field("username") String username, @Field("email") String email, @Field("password") String password, @Field("phone") String phone, @Field("address") String address, @Field("gender") String gender,@Field("isAdmin") Boolean isAdmin);

    @Multipart
    @PATCH("api/backend/v1/users/{id}")
    Call<ResponseBody> edit(@Path("id") Integer id, @PartMap Map<String, RequestBody> Params);

    @Headers("Content-Type: application/json")
    @PATCH("api/backend/v1/users/change_password/{id}")
    Call<ResponseBody> change_password(@Path("id") Integer id, @Body String user);
}
