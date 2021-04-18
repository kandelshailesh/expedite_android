package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.adapters.CartAdapter;
import com.example.basic.R;
import com.example.interfaces.OrderInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class ProductCartFragment extends Fragment {
    ImageView image;
    TextView name, description, cost;
    Bundle bundle;
    ImageButton addBtn, minusBtn;
    EditText quantity;
    Integer min_quantity = 0;
    Integer max_quantity = 0;
    Button cartBtn, subscribeBtn;
    Double gross_amount = 0.0, discount = 0.0, shipping_charge = 0.0, total_amount = 0.0;
    JSONObject orderData = new JSONObject();
    ProgressBar progressBar;
    ScrollView scrollView;
    LinearLayout linearLayout;

    public ProductCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Opening Details", "Shailesh");
        bundle = this.getArguments();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(container.GONE);
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        image = view.findViewById(R.id.cart_image);
        name = view.findViewById(R.id.cart_pname);
        description = view.findViewById(R.id.cart_pdescription);
        cost = view.findViewById(R.id.cart_pprice);
        addBtn = view.findViewById(R.id.addBtn);
        minusBtn = view.findViewById(R.id.minusBtn);
        quantity = view.findViewById(R.id.cart_quantity);
        cartBtn = view.findViewById(R.id.cartBtn);
        subscribeBtn = view.findViewById(R.id.subscribeBtn);
        progressBar = view.findViewById(R.id.product_progess);
        scrollView = view.findViewById(R.id.cart_scrollview);
        linearLayout = view.findViewById(R.id.cart_bottom);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(bundle.getString("name"));
        ;
        return view;
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
//        navBar.setVisibility(View.VISIBLE);
//    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CartManagement a = new CartManagement();
        super.onViewCreated(view, savedInstanceState);
        Integer user_id = null;
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
        a.fetchOrder(user_id);
        Retrofit retrofit = new Network().getRetrofit1();
        OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
        Call<ResponseBody> call = jsonPlaceholder.fetch(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    if (bundle != null) {
                        Log.d("Bundle", bundle.toString());
                        String name1 = bundle.get("name").toString();
                        String description1 = bundle.get("description").toString();
                        String imageUrl1 = bundle.get("imageUrl").toString();
                        String cost1 = bundle.get("price").toString();
                        min_quantity = bundle.getInt("min_quantity");
                        max_quantity = bundle.getInt("max_quantity");
                        name.setText(name1);
                        cost.setText("Rs " + cost1);
                        description.setText(description1);
                        Picasso.with(getView().getContext()).load(Uri.parse(imageUrl1)).into(image);
                        quantity.setText(Integer.toString(min_quantity));
                    }

                    try {
                        String re = response.body().string();
                        JSONObject obj = new JSONObject(re);
                        Log.d("Orders", obj.toString());
                        if (obj.getJSONArray("DATA").length() > 0) {
                            orderData = (JSONObject) obj.getJSONArray("DATA").get(0);
                            JSONArray a = orderData.getJSONArray("orders_items");
                            JSONObject b = new JSONObject();
                            for (int i = 0; i < a.length(); i++) {
                                b = (JSONObject) a.get(i);
                                Integer p_id = b.getInt("product_id");
                                if (bundle.getInt("product_id") == p_id) {
                                    quantity.setText(Integer.toString(b.getInt("quantity")));
                                    break;
                                }
                            }
                        }
                        scrollView.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
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


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(quantity.getText().toString()) < max_quantity && Integer.parseInt(quantity.getText().toString()) >= min_quantity) {
                    quantity.setText(Integer.toString(Integer.parseInt(quantity.getText().toString()) + 1));
                } else {
                    Toast.makeText(getContext(), "Maximum quantity limit ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(quantity.getText().toString()) > min_quantity) {
                    quantity.setText(Integer.toString(Integer.parseInt(quantity.getText().toString()) - 1));
                } else {
                    Toast.makeText(getContext(), "Mininum quantity limit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Integer finalUser_id = user_id;
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartBtn.setAlpha(0.5f);
                cartBtn.setEnabled(false);
                try {
                    Integer product_id = bundle.getInt("product_id");
                    Integer quantity1 = Integer.parseInt(quantity.getText().toString());
                    Double price = Double.parseDouble(bundle.get("price").toString());
                    Integer user_id = finalUser_id;
                    if (orderData != null && orderData.has("orders_items") && orderData.getJSONArray("orders_items").length() > 0) {
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
                            b.put("quantity", quantity1);
                            gross_amount = gross_amount - prev_quantity * price + quantity1 * price;
                        } else {
                            b.put("product_id", product_id);
                            b.put("quantity", quantity1);
                            b.put("price", price);
                            gross_amount = gross_amount + quantity1 * price;
                        }
                        total_amount = gross_amount;
                        a.put(i, b);
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
                                cartBtn.setEnabled(true);
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
                            }
                        });
                    } else {
                        JSONArray a = new JSONArray();
                        JSONObject b = new JSONObject();
                        b.put("quantity", quantity1);
                        gross_amount = gross_amount + quantity1 * price;
                        total_amount = gross_amount;
                        b.put("product_id", product_id);
                        b.put("quantity", quantity1);
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
        });

        subscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 =new Bundle();
                bundle1.putInt("id",bundle.getInt("product_id"));
                bundle1.putString("name",bundle.getString("name"));
                Navigation.findNavController(getView()).navigate(R.id.nav_create_subscription,bundle1);
            }
        });
    }
}
