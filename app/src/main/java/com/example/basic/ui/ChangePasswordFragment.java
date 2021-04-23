package com.example.basic.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.basic.MainActivity;
import com.example.basic.R;
import com.example.interfaces.Users;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordFragment extends Fragment {
    Integer user_id;
    EditText old_password, new_password;
    Button changePasswordBtn;
    AwesomeValidation awesomeValidation=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_pasword, container, false);
        old_password = root.findViewById(R.id.edit_password);
        new_password = root.findViewById(R.id.edit_newpassword);
        changePasswordBtn = root.findViewById(R.id.profile_editPassword);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.edit_password, RegexTemplate.NOT_EMPTY, R.string.old_password);
        awesomeValidation.addValidation(getActivity(), R.id.edit_newpassword, RegexTemplate.NOT_EMPTY, R.string.new_password);
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    updateUser(view);
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String user_info = sharedPreferences.getString("user_info", "");
        String image_url = null;
        if (user_info.isEmpty()) {
            Log.d("User", "Not found");
        } else {
            try {
                JSONObject s = new JSONObject(user_info);
                user_id = s.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public void updateUser(View v) {
        try {
            Retrofit retrofit = new Network().getRetrofit1();
            Users jsonPlaceholder = retrofit.create(Users.class);
            JSONObject obj = new JSONObject();
            obj.put("password",old_password.getText().toString());
            obj.put("new_password", new_password.getText().toString());
            Call<ResponseBody> call = jsonPlaceholder.change_password(user_id, obj.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String re = response.body().string();
//                            SharedPreferences settings = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
//                            settings.edit().remove("user_info").commit();
                            Toast.makeText(getActivity(), "Password changed successfully", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(getView()).navigate(R.id.nav_profile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ApiError error = ErrorUtils.parseError(retrofit, response);
                        Log.d("Error", error.getError());
                        Toast.makeText(getActivity(), error.getError(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (RuntimeException | JSONException err) {

            Log.d("Err", err.toString());
        }
    }
}