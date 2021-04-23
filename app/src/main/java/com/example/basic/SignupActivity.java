package com.example.basic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.interfaces.Users;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.models.users.Register;
import com.example.network.Network;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {
    private TextView fullName,username,email,password,cpassword,phone,address;
    CheckBox acceptCheck;
    RadioGroup gender;
    Button signupBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        cpassword =findViewById(R.id.cpassword);
        phone= findViewById(R.id.phone);
        address=findViewById(R.id.address);
        gender=findViewById(R.id.gender);
        acceptCheck=findViewById(R.id.acceptCheck);
        signupBtn= findViewById(R.id.signupBtn);
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.fullName, RegexTemplate.NOT_EMPTY,R.string.fullName);
        awesomeValidation.addValidation(this, R.id.fullName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.invalid_fullname);
        awesomeValidation.addValidation(this,R.id.email, RegexTemplate.NOT_EMPTY,R.string.email);
        awesomeValidation.addValidation(this,R.id.email, Patterns.EMAIL_ADDRESS,R.string.invalid_email);
        awesomeValidation.addValidation(this,R.id.username, RegexTemplate.NOT_EMPTY,R.string.username);
        awesomeValidation.addValidation(this,R.id.password, RegexTemplate.NOT_EMPTY,R.string.password);
        awesomeValidation.addValidation(this,R.id.cpassword, R.id.password,R.string.cpassword);
        awesomeValidation.addValidation(this,R.id.phone, RegexTemplate.NOT_EMPTY,R.string.phone);
        awesomeValidation.addValidation(this, R.id.phone, "^[2-9]{2}[0-9]{8}$", R.string.invalid_phone);
        awesomeValidation.addValidation(this,R.id.address, RegexTemplate.NOT_EMPTY,R.string.address);
        awesomeValidation.addValidation(this,R.id.gender, RegexTemplate.NOT_EMPTY,R.string.gender);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(awesomeValidation.validate()) {
                    signupBtn.setEnabled(false);
                    Signup();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter all the fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openLogin(View v)
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void Signup()
    {
        Retrofit retrofit = new Network().getRetrofit();
        Users users = retrofit.create(Users.class);
        Call<ResponseBody> call = users.signup(fullName.getText().toString(),username.getText().toString(),email.getText().toString(),password.getText().toString(),phone.getText().toString(),address.getText().toString(),((RadioButton)findViewById(gender.getCheckedRadioButtonId())).getText().toString().toLowerCase(),false);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Response",new Gson().toJson(response.body()));
                if(response.isSuccessful()) {
                    signupBtn.setEnabled(true);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Log.d("Login_call", response.code() + "");
                    ApiError error = ErrorUtils.parseError(retrofit, response);
                    Log.d("Error",error.getError());
                    Toast.makeText(SignupActivity.this,error.getError(),Toast.LENGTH_SHORT).show();
                    Log.d("Login_call_error", new Gson().toJson(error));
                    signupBtn.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                signupBtn.setEnabled(true);
                Log.d("Error",t.toString());
            }
        });
    }
}