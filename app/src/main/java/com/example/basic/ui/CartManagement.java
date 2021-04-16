package com.example.basic.ui;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapters.CategoryAdapter;
import com.example.interfaces.CategoryInterface;
import com.example.interfaces.OrderInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartManagement {
    Double gross_amount = 0.0, discount = 0.0, shipping_charge = 0.0, total_amount = 0.0;
    JSONObject orderData = new JSONObject();

    public void fetchOrder(Integer user_id) {
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

    public void addtoCart(Integer product_id, Integer quantity, Double price, Integer user_id) {
        try {
            if (orderData != null && orderData.length() > 0) {
                JSONArray a = orderData.getJSONArray("orders_items");
                gross_amount = orderData.getDouble("gross_amount");
                discount = orderData.getDouble("discount");
                shipping_charge = orderData.getDouble("shipping_charge");
                total_amount = orderData.getDouble("total_amount");
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
                    total_amount = gross_amount;
                    a.put(i, b);
                } else {
                    b.put("product_id", product_id);
                    b.put("quantity", quantity);
                    b.put("price", price);
                    gross_amount = gross_amount + quantity * price;
                    total_amount = gross_amount;
                    a.put(i, b);
                }
                orderData.put("gross_amount", gross_amount);
                orderData.put("total_amount", total_amount);
                orderData.put("status", "PENDING");
                orderData.put("order_item", a);
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String re = response.body().string();
                                JSONObject obj = new JSONObject(re);
                                orderData = obj.getJSONObject("DATA");
                                Log.d("Order", orderData.toString());
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
                orderData.put("gross_amount", gross_amount);
                orderData.put("discount", 0);
                orderData.put("total_quantity", 0);
                orderData.put("total_amount", total_amount);
                orderData.put("order_item", a);
                orderData.put("user_id", 1);
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String re = response.body().string();
                                Log.d("Order", re);
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void deleteCartItem(Integer product_id) {
        try {
            if (orderData.length() > 0) {
                JSONArray a = orderData.getJSONArray("orders_items");
                gross_amount = orderData.getDouble("gross_amount");
                discount = orderData.getDouble("discount");
                shipping_charge = orderData.getDouble("shipping_charge");
                total_amount = orderData.getDouble("total_amount");
                Integer present = 0, i;
                JSONObject b = null;
                for (i = 0; i < a.length(); i++) {
                    b = (JSONObject) a.get(i);
                    Integer p_id = b.getInt("product_id");
                    Integer prev_quantity = b.getInt("quantity");
                    Integer price = b.getInt("price");
                    a.remove(i);
                    if (p_id == product_id) {
                        gross_amount = gross_amount - prev_quantity * price;
                        total_amount = gross_amount;
                        break;
                    }
                }
                orderData.put("gross_amount", gross_amount);
                orderData.put("total_amount", total_amount);
                orderData.put("orders_items", a);
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Call<ResponseBody> call = jsonPlaceholder.create(orderData.toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String re = response.body().string();
                                JSONObject obj = new JSONObject(re);
                                orderData = obj.getJSONObject("DATA");
                                Log.d("Order", orderData.toString());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkOut(Integer user_id) {

    }

}
