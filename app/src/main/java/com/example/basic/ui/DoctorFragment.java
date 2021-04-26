
package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CartAdapter;
import com.example.adapters.DoctorAdapter;
import com.example.basic.R;
import com.example.interfaces.DoctorInterface;
import com.example.interfaces.OrderInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DoctorFragment extends Fragment {
    TextView result;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doctor, container, false);
        result = root.findViewById(R.id.doctor_result);
        recyclerView = root.findViewById(R.id.rvDoctor);
        progressBar =root.findViewById(R.id.doctor_progress);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Retrofit retrofit = new Network().getRetrofit1();
        DoctorInterface jsonPlaceholder = retrofit.create(DoctorInterface.class);
        Call<ResponseBody> call = jsonPlaceholder.fetch();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    try {
                        String re = response.body().string();
                        JSONObject obj = new JSONObject(re);
                        Log.d("Orders", obj.toString());
                        if (obj.getJSONArray("DATA").length() > 0) {
                            result.setVisibility(View.GONE);
                            DoctorAdapter myAdapter = null;
                            myAdapter = new DoctorAdapter(getContext(), obj.getJSONArray("DATA"));
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        } else {
                            result.setVisibility(View.VISIBLE);
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ApiError error = ErrorUtils.parseError(retrofit, response);
                    Log.d("Error", error.getError());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }

}