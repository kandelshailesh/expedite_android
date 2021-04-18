package com.example.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.BuildConfig;
import com.example.basic.R;
import com.example.basic.ui.OrderFragment;
import com.example.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderProductDetailsAdapter extends RecyclerView.Adapter<OrderProductDetailsAdapter.OrderHolder> {
    Context context;
    JSONArray category;

    public OrderProductDetailsAdapter(Context ct, JSONArray p) {
        context = ct;
        category = p;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_order_rows, parent, false);
        return new OrderHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            String name = a.getJSONObject("product").getString("name");
            Double price = a.getJSONObject("product").getDouble("unit_price");
            String imageUrl = BuildConfig.API_URL+'/'+a.getJSONObject("product").getString("image");
            Integer quantity = a.getInt("quantity");
            holder.name.setText(name);
            holder.price.setText("Rs."+price.toString());
            holder.quantity.setText("Qty: "+quantity.toString());
            Picasso.with(context).load(Uri.parse(imageUrl)).into(holder.imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return category.length();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        public ItemClickListener listener;
        ImageView imageView;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.order_pname);
            price = itemView.findViewById(R.id.order_pprice);
            quantity = itemView.findViewById(R.id.order_pquantity);
            imageView= itemView.findViewById(R.id.order_image);
        }

        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}