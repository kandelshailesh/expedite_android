package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.basic.R;
import com.example.interfaces.Users;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditProfileFragment extends Fragment {
    Integer user_id;
    TextView fullName, address, phone;
    Button editBtn;
    RadioGroup gender;

    AwesomeValidation awesomeValidation=null;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        fullName = root.findViewById(R.id.profile_editfullName);
        address = root.findViewById(R.id.profile_editaddress);
        phone = root.findViewById(R.id.profile_editphone);
        editBtn = root.findViewById(R.id.profile_editBtn1);
        gender = root.findViewById(R.id.profile_editgender);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editfullName, RegexTemplate.NOT_EMPTY, R.string.fullName);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editphone, RegexTemplate.NOT_EMPTY, R.string.phone);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editaddress, RegexTemplate.NOT_EMPTY, R.string.address);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editgender, RegexTemplate.NOT_EMPTY, R.string.gender);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editfullName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.invalid_fullname);
        awesomeValidation.addValidation(getActivity(), R.id.profile_editphone, "^[2-9]{2}[0-9]{8}$", R.string.invalid_phone);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(awesomeValidation.validate()) {
                    updateUser(view);
                }
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String user_info = sharedPreferences.getString("user_info", "");
        String image_url = null;
        if (user_info.isEmpty()) {
            Log.d("User", "Not found");
        } else {
            try {
                JSONObject s = new JSONObject(user_info);
                user_id = s.getInt("id");
                fullName.setText(s.getString("fullName"));
                address.setText(s.getString("address"));
                phone.setText(s.getString("phone"));
                String getGender = s.getString("gender");
                if (getGender == "male") {
                    gender.check(R.id.profile_editMale);
                } else {
                    gender.check(R.id.profile_editFemale);
                }
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
            Map<String, RequestBody> map = new HashMap<>();
            map.put("fullName", toRequestBody(fullName.getText().toString()));
            map.put("address", toRequestBody(address.getText().toString()));
            map.put("phone", toRequestBody(phone.getText().toString()));
            map.put("gender", toRequestBody(((RadioButton) getView().findViewById(gender.getCheckedRadioButtonId())).getText().toString().toLowerCase()));
            Call<ResponseBody> call = jsonPlaceholder.edit(user_id, map);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String re = response.body().string();
                            JSONObject obj = null;
                            obj = new JSONObject(re);
                            String user = null;
                            user = obj.getJSONObject("data").toString();
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user_info", user);
                            editor.apply();
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(getView()).navigate(R.id.nav_profile);
                        } catch (IOException | JSONException e) {
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
        } catch (RuntimeException err) {

            Log.d("Err", err.toString());
        }
    }
}