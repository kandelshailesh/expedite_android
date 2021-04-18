
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
import com.example.adapters.SubscriptionAdapter;
import com.example.basic.R;
import com.example.interfaces.CartInterface;
import com.example.interfaces.OrderInterface;
import com.example.interfaces.SubscriptionInterface;
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

public class SubscriptionFragment extends Fragment {
    TextView result, totalAmount;
    RecyclerView recyclerView;
    Integer user_id;
    ProgressBar progressBar;
    JSONArray orderData = new JSONArray();
    BottomNavigationView navBar;
    ViewGroup con;
    Double gross_amount = 0.0, discount = 0.0, shipping_charge = 0.0, total_amount = 0.0;
    JSONObject orderData1 = new JSONObject();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_subscription, container, false);
        navBar = getActivity().findViewById(R.id.bottom_navigation);
        result = root.findViewById(R.id.subscription_result);
        recyclerView = root.findViewById(R.id.rvSubscription);
        progressBar = root.findViewById(R.id.subscription_progress);
        con = container;
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
                if (response.isSuccessful()) {
                    try {
                        String re = response.body().string();
                        JSONObject obj = new JSONObject(re);
                        Log.d("Orders", obj.toString());
                        if (obj.getJSONArray("DATA").length() > 0) {
                            orderData1 = (JSONObject) obj.getJSONArray("DATA").get(0);
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

    public void fetchSubscriptionItem() {
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
        SubscriptionInterface jsonPlaceholder = retrofit.create(SubscriptionInterface.class);
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
                            orderData = obj.getJSONArray("DATA");
                            SubscriptionAdapter myAdapter = null;
                            myAdapter = new SubscriptionAdapter(getContext(), orderData, SubscriptionFragment.this);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        fetchCartItem();
        fetchSubscriptionItem();
    }


    public void updateSubscription(View v, Integer position, Integer quantity, Double price) {
        try {
            v.findViewById(R.id.linearCartrows).setEnabled(false);
            JSONObject b = (JSONObject) orderData.get(position);
            Integer prev_quantity = b.getInt("quantity");
            b.remove("quantity");
            b.put("quantity", quantity);
            Retrofit retrofit = new Network().getRetrofit1();
            SubscriptionInterface jsonPlaceholder = retrofit.create(SubscriptionInterface.class);
            Call<ResponseBody> call = jsonPlaceholder.update(b.getInt("id"), b.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    recyclerView.findViewById(R.id.linearCartrows).setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            String re = response.body().string();
                            Log.d("Order", re);
                            fetchSubscriptionItem();
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
    public void deleteSubscription(View v, Integer position, Integer quantity, Double price) {
        try {
            v.findViewById(R.id.linearCartrows).setEnabled(false);
            JSONObject b = (JSONObject) orderData.get(position);
            Retrofit retrofit = new Network().getRetrofit1();
            SubscriptionInterface jsonPlaceholder = retrofit.create(SubscriptionInterface.class);
            Call<ResponseBody> call = jsonPlaceholder.delete(b.getInt("id"));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    recyclerView.findViewById(R.id.linearCartrows).setEnabled(true);
                    if (response.isSuccessful()) {
                        try {
                            Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
                            String re = response.body().string();
                            Log.d("Order", re);
                            fetchSubscriptionItem();
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


    public void addtoCart(View v, Integer position, Integer quantity, Double price,Integer product_id) {
        try {
            v.findViewById(R.id.subscriptionAddBtn).setEnabled(false);
            v.findViewById(R.id.subscriptionAddBtn).setAlpha(0.5f);
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
            if (orderData1 != null && orderData1.has("orders_items") && orderData1.getJSONArray("orders_items").length() > 0) {
                JSONArray a = orderData1.getJSONArray("orders_items");
                gross_amount = orderData1.getDouble("gross_amount");
                discount = orderData1.getDouble("discount");
                shipping_charge = orderData1.getDouble("shipping_charge");
                total_amount = orderData1.getDouble("total_amount");
                Integer present = 0, i;
                JSONObject b = new JSONObject();
                for (i = 0; i < a.length(); i++) {
                    b = (JSONObject) a.get(i);
                    Integer p_id = b.getInt("product_id");
                    if (p_id == product_id) {
                        present = 1;
                        break;
                    }
                }

                Log.d("Present", String.valueOf(present));
                if (present == 1) {
                    Integer prev_quantity = b.getInt("quantity");
                    b.remove("quantity");
                    b.put("quantity", quantity);
                    gross_amount = gross_amount - prev_quantity * price + quantity * price;
                } else {
                    b.put("product_id", product_id);
                    b.put("quantity", quantity);
                    b.put("price", price);
                    gross_amount = gross_amount + quantity * price;
                }
                total_amount = gross_amount;
                a.put(i, b);
                orderData1.put("gross_amount", gross_amount);
                orderData1.put("total_amount", total_amount);
                orderData1.put("status", "PENDING");
                orderData1.put("order_item", a);
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        v.findViewById(R.id.subscriptionAddBtn).setEnabled(false);
                        v.findViewById(R.id.subscriptionAddBtn).setAlpha(0.5f);

                        if (response.isSuccessful()) {
                            try {
                                Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_LONG).show();
                                String re = response.body().string();
                                Log.d("Order", re);
                                v.findViewById(R.id.subscriptionAddBtn).setEnabled(true);
                                v.findViewById(R.id.subscriptionAddBtn).setAlpha(1f);
                                Navigation.findNavController(getView()).navigate(R.id.nav_cart);
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
            } else {
                JSONArray a = new JSONArray();
                JSONObject b = new JSONObject();
                b.put("quantity", quantity);
                gross_amount = gross_amount + quantity * price;
                total_amount = gross_amount;
                b.put("product_id", product_id);
                b.put("quantity", quantity);
                b.put("price", price);
                a.put(0, b);
                orderData1.put("gross_amount", gross_amount);
                orderData1.put("discount", 0);
                orderData1.put("total_quantity", 0);
                orderData1.put("total_amount", total_amount);
                orderData1.put("order_item", a);
                orderData1.put("user_id", user_id);
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Call<ResponseBody> call = jsonPlaceholder.create(orderData1.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                Toast.makeText(getContext(), "Added to cart successfully", Toast.LENGTH_LONG).show();
                                String re = response.body().string();
                                Log.d("Order", re);
                                Navigation.findNavController(getView()).navigate(R.id.nav_cart);
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
                        Log.d("Error", t.toString());
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            Log.d("Error", e.toString());
            Toast.makeText(getContext(), "Error in adding to cart", Toast.LENGTH_LONG).show();

        }
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
//        navBar.setVisibility(View.VISIBLE);
//    }
}