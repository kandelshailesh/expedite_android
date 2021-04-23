package com.example.basic;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.adapters.MyAdapter;
import com.example.interfaces.JSONPlaceholder;
import com.example.interfaces.Users;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.models.posts.Post;
import com.example.models.users.Login;
import com.example.network.Network;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    EditText username,password;
    Button loginBtn;
AwesomeValidation awesomeValidation;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int colorCodeDark = Color.parseColor("#CC0000");
        window.setStatusBarColor(colorCodeDark);
       // setTitle("Login");
        SharedPreferences sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("token")) {
            String token = sharedPreferences.getString("token", "");
            if (!token.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        }
      else {
            setContentView(R.layout.activity_main);
            username = findViewById(R.id.username);
            password = findViewById(R.id.password);
            loginBtn = findViewById(R.id.loginBtn);
            username.setText("admin1@gmail.com");
            password.setText("admin");
            awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            awesomeValidation.addValidation(this, R.id.username, RegexTemplate.NOT_EMPTY, R.string.invalid_username);
            awesomeValidation.addValidation(this, R.id.password, RegexTemplate.NOT_EMPTY, R.string.invalid_password);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginBtn.setEnabled(false);
                    if (awesomeValidation.validate()) {
                        Login();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter valid information", Toast.LENGTH_SHORT).show();
                        loginBtn.setEnabled(true);
                    }
                }
            });
        }
    }



    public  void Login() {
//        result = findViewById(R.id.postList);
        Retrofit retrofit = new Network().getRetrofit();
        Users users = retrofit.create(Users.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",username.getText().toString());
            jsonObject.put("password",password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = users.login(jsonObject.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Response",new Gson().toJson(response.errorBody()));
                if(response.isSuccessful()) {
                    loginBtn.setEnabled(true);
                    String re= null;
                    try {
                        re = response.body().string();
                        JSONObject obj = null;
                        obj = new JSONObject(re);
                        String token = null;
                        token = obj.getString("token");
                        String user= null;
                        user= obj.getJSONObject("data").toString();
                        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token",token);
                        editor.putString("user_info",user);
                        editor.apply();
                        Log.d("Token",token);
                        Log.d("User",user);
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Log.d("Login_call", response.code() + "");
                    ApiError error = ErrorUtils.parseError(retrofit, response);
                    Log.d("Error",error.getError());
                    Toast.makeText(MainActivity.this,error.getError(),Toast.LENGTH_SHORT).show();
                    Log.d("Login_call_error", new Gson().toJson(error));
                    loginBtn.setEnabled(true);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                loginBtn.setEnabled(true);
                Log.d("Error",t.toString());
            }
        });
    }

    public void openSignup(View v)
    {
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }
    protected void onClick(View v)
    {
        v.setBackgroundColor(333);
    }
}