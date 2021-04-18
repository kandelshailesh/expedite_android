
package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.CartAdapter;
import com.example.adapters.OrderAdapter;
import com.example.basic.R;
import com.example.interfaces.CartInterface;
import com.example.interfaces.OrderInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderFragment extends Fragment {
    TextView result;
    RecyclerView recyclerView;
    Integer user_id;
    ProgressBar progressBar;
    JSONArray orderData = new JSONArray();
    ViewGroup con;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        result = root.findViewById(R.id.order_result);
        recyclerView = root.findViewById(R.id.rvorder);
        progressBar = root.findViewById(R.id.order_progress);
        return root;
    }

    public void fetchOrders() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String user_info = sharedPreferences.getString("user_info", "");
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
        Retrofit retrofit = new Network().getRetrofit1();
        OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
        Call<ResponseBody> call = jsonPlaceholder.fetchSuccess(user_id);
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
                            orderData = (JSONArray) obj.getJSONArray("DATA");
                            OrderAdapter myAdapter = null;
                            myAdapter = new OrderAdapter(getContext(),orderData,OrderFragment.this);
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        } else {
                            result.setVisibility(View.VISIBLE);
                        }
                        ;
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


    public void gotoDetails(View v,Integer position)
    {
        try {
            JSONObject orderDetails =(JSONObject) orderData.get(0);
            Bundle bundle = new Bundle();
            bundle.putString("order_details",orderDetails.toString());
            Navigation.findNavController(getView()).navigate(R.id.nav_order_details,bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        fetchOrders();
    }

}