
package com.example.basic.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapters.OrderProductDetailsAdapter;
import com.example.basic.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailsFragment extends Fragment {
    ProgressBar progressBar;
    JSONArray orderData = new JSONArray();
    ViewGroup con;
    Bundle bundle;
    Integer user_id;
    LinearLayout linearLayout;
    TextView orderId, orderedDate, total_amount, gross_amount, shipping_charge, billing_address, shipping_address;
    RecyclerView recyclerView;
    BottomNavigationView navBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders_details, container, false);
        progressBar = root.findViewById(R.id.order_details_progress);
        linearLayout = root.findViewById(R.id.order_details_layout);
        orderId = root.findViewById(R.id.order_id);
        orderedDate = root.findViewById(R.id.ordered_date);
        total_amount = root.findViewById(R.id.total_amount);
        gross_amount = root.findViewById(R.id.gross_amount);
        shipping_address = root.findViewById(R.id.shipping_address);
        billing_address = root.findViewById(R.id.billing_address);
        shipping_charge = root.findViewById(R.id.shipping_charge);
        recyclerView = root.findViewById(R.id.rvorderItem);
        bundle = this.getArguments();
        navBar= getActivity().findViewById(R.id.bottom_navigation);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        Log.d("Bundle", bundle.toString());
        navBar.setVisibility(View.GONE);
        if (bundle != null) {
            JSONObject category = null;
            try {
                category = new JSONObject(bundle.getString("order_details"));
                Integer orderId1 = category.getInt("id");
                String orderDate1 = category.getString("ordered_date");
                Double total_amount1 = category.getDouble("total_amount");
                Double shipping_charge1 = category.getDouble("shipping_charge");
                Double gross_amount1 = category.getDouble("gross_amount");
                String billingAddress1 = category.getJSONObject("address").getString("billing");
                String shippingAddress1 = category.getJSONObject("address").getString("shipping");
                JSONArray orderDetails1= category.getJSONArray("orders_items");
                OrderProductDetailsAdapter orderDetailsAdapter = new OrderProductDetailsAdapter(getContext(), category.getJSONArray("orders_items"));
                orderId.setText("Order ID: #" + orderId1);
                orderedDate.setText(orderDate1);
                shipping_address.setText(shippingAddress1);
                billing_address.setText(billingAddress1);
                gross_amount.setText("Rs." + gross_amount1);
                shipping_charge.setText("Rs. " + shipping_charge1);
                total_amount.setText("Rs. " + total_amount1);
                recyclerView.setAdapter(orderDetailsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}