package com.example.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.BuildConfig;
import com.example.basic.R;
import com.example.basic.ui.MainCartFragment;
import com.example.basic.ui.OrderFragment;
import com.example.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    Context context;
    JSONArray category;
    OrderFragment cartInterface;

    public OrderAdapter(Context ct, JSONArray p, OrderFragment f) {
        context = ct;
        category = p;
        cartInterface=f;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_orders_rows, parent, false);
        return new OrderHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            Log.d("From", a.toString());
            Integer orderId = a.getInt("id");
            String orderDate = a.getString("ordered_date");
            holder.orderId.setText("Order ID: #"+orderId);
            holder.orderedDate.setText(orderDate.toString());
            holder.viewDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.gotoDetails(holder.itemView,position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
      return category.length();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView orderId,orderedDate,viewDetailsBtn;
        public ItemClickListener listener;
        public OrderHolder(@NonNull View itemView) {
            super(itemView);
           orderId=itemView.findViewById(R.id.order_id);
           orderedDate= itemView.findViewById(R.id.ordered_date);
           viewDetailsBtn= itemView.findViewById(R.id.viewDetails);
        }
        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}