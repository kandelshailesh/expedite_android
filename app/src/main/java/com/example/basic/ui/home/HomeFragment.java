package com.example.basic.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CategoryAdapter;
import com.example.basic.R;
import com.example.interfaces.CategoryInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {
    TextView result;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        result = root.findViewById(R.id.category_result);
        recyclerView= root.findViewById(R.id.rvCategory);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created","Home fragment");
        Retrofit retrofit = new Network().getRetrofit1();
        CategoryInterface jsonPlaceholder = retrofit.create(CategoryInterface.class);
        Call<ResponseBody> call = jsonPlaceholder.fetch("true");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    try {
                        result.setVisibility(View.GONE);
                        String re= response.body().string();
                        JSONObject obj = new JSONObject(re);
                        CategoryAdapter myAdapter = new CategoryAdapter(getContext(), obj.getJSONArray("DATA"));
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    result.setVisibility(View.VISIBLE);
                    ApiError error = ErrorUtils.parseError(retrofit, response);
                    Log.d("Error",error.getError());
                    Toast.makeText(getActivity(),error.getError(),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setText(t.getMessage());
            }
        });
    }
}