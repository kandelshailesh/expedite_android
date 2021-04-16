
package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CartAdapter;
import com.example.adapters.CategoryAdapter;
import com.example.basic.R;
import com.example.interfaces.CategoryInterface;
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

public class MainCartFragment extends Fragment {
    TextView result;
    RecyclerView recyclerView;
    Integer user_id;
    CartManagement cartManagement = new CartManagement();
    JSONObject orderData = new JSONObject();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maincart, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(container.GONE);
        result = root.findViewById(R.id.cart_result);
        recyclerView= root.findViewById(R.id.rvCart);
        cartManagement.fetchOrder(1);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created","Home fragment");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String user_info = sharedPreferences.getString("user_info", "");
        if (user_info.isEmpty()) {
            Log.d("User", "Not found");
        } else {
            try {
                JSONObject s = new JSONObject(user_info);
                user_id= s.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Retrofit retrofit = new Network().getRetrofit1();
        OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
        Call<ResponseBody> call = jsonPlaceholder.fetch(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String re = response.body().string();
                        JSONObject obj = new JSONObject(re);
                        Log.d("Orders", obj.toString());
                        if (obj.getJSONArray("DATA").length() > 0) {
                            orderData = (JSONObject) obj.getJSONArray("DATA").get(0);
                        }
                        CartAdapter myAdapter = null;
                        myAdapter = new CartAdapter(getContext(), orderData);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);
    }
}