
package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.adapters.CategoryAdapter;
import com.example.basic.R;
import com.example.interfaces.CartInterface;
import com.example.interfaces.CategoryInterface;
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

public class MainCartFragment extends Fragment implements CartInterface {
    TextView result, totalAmount;
    RecyclerView recyclerView;
    Integer user_id;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    JSONObject orderData = new JSONObject();
    BottomNavigationView navBar;
    ViewGroup con;
    Button checkoutBtn,subscribeBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_maincart, container, false);
        navBar= getActivity().findViewById(R.id.bottom_navigation);
        result = root.findViewById(R.id.cart_result);
        recyclerView = root.findViewById(R.id.rvCart);
        progressBar = root.findViewById(R.id.cart_progress);
        totalAmount = root.findViewById(R.id.cart_totalAmount);
        linearLayout = root.findViewById(R.id.maincart_buttom);
        con=container;
        checkoutBtn=root.findViewById(R.id.checkoutBtn);
        return root;
    }


    public void fetchCartItem() {
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
        Call<ResponseBody> call = jsonPlaceholder.fetch(user_id);
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
                            linearLayout.setVisibility(View.VISIBLE);
                            navBar.setVisibility(con.GONE);
                            orderData = (JSONObject) obj.getJSONArray("DATA").get(0);
                            totalAmount.setText("Rs." + Double.toString(orderData.getDouble("total_amount")));
                            CartAdapter myAdapter = null;
                            myAdapter = new CartAdapter(getContext(), orderData, MainCartFragment.this);
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        } else {
                            navBar.setVisibility(con.VISIBLE);
                            result.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                        } ;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        fetchCartItem();
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("orders",orderData.toString());
                Navigation.findNavController(getView()).navigate(R.id.nav_checkout,bundle);
            }
        });
    }


    @Override
    public void addtoCart(View v, Integer position, Integer quantity, Double price) {
        try {
            v.findViewById(R.id.linearCartrows).setEnabled(false);
            JSONArray jsonArray = orderData.getJSONArray("orders_items");
            JSONObject b = (JSONObject) jsonArray.get(position);
            Integer prev_quantity = b.getInt("quantity");
            b.remove("quantity");
            b.put("quantity", quantity);
            Double gross_amount = orderData.getDouble("gross_amount");
            gross_amount = gross_amount - prev_quantity * price + quantity * price;
            jsonArray.put(position, b);
            orderData.put("gross_amount", gross_amount);
            orderData.put("total_amount", gross_amount);
            orderData.put("order_item", jsonArray);
            Retrofit retrofit = new Network().getRetrofit1();
            OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
            Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
            Double finalGross_amount = gross_amount;
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    recyclerView.findViewById(R.id.linearCartrows).setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_LONG).show();
                            String re = response.body().string();
                            Log.d("Order", re);
                            totalAmount.setText("Rs." + Double.toString(finalGross_amount));
                            fetchCartItem();
                        } catch (IOException e) {
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
        } catch (JSONException e) {
            Log.d("dgdg", e.toString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void deletefromCart(View v, Integer position, Integer quantity, Double price) {
        try {
            v.findViewById(R.id.linearCartrows).setEnabled(false);
            JSONArray jsonArray = orderData.getJSONArray("orders_items");
            JSONArray jsonArray1 = new JSONArray();
            Integer getId = ((JSONObject) jsonArray.get(position)).getInt("id");
            jsonArray1.put(getId);
            jsonArray.remove(position);
            Double gross_amount = orderData.getDouble("gross_amount");
            gross_amount = gross_amount - quantity * price;
            orderData.put("gross_amount", gross_amount);
            orderData.put("total_amount", gross_amount);
            orderData.put("order_item", jsonArray);
            orderData.put("deleted_item", jsonArray1);
            Retrofit retrofit = new Network().getRetrofit1();
            OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
            Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
            Double finalGross_amount = gross_amount;
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    recyclerView.findViewById(R.id.linearCartrows).setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_LONG).show();
                            String re = response.body().string();
                            Log.d("Order", re);
                            totalAmount.setText("Rs." + Double.toString(finalGross_amount));
                            fetchCartItem();
                        } catch (IOException e) {
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
        } catch (JSONException e) {
            Log.d("dgdg", e.toString());
        }
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
//        navBar.setVisibility(View.VISIBLE);
//    }
}