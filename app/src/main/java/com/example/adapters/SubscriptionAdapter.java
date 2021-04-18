package com.example.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.BuildConfig;
import com.example.basic.R;
import com.example.basic.ui.MainCartFragment;
import com.example.basic.ui.SubscriptionFragment;
import com.example.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.CartHolder> {
    Context context;
    JSONArray category;
    SubscriptionFragment cartInterface;

    public SubscriptionAdapter(Context ct, JSONArray p, SubscriptionFragment f) {
        context = ct;
        category = p;
        cartInterface = f;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_subscribed_rows, parent, false);
        return new CartHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            Log.d("From", a.toString());
            String name = a.getJSONObject("product").getString("name");
            Double price = a.getJSONObject("product").getDouble("unit_price");
            Integer quantity = a.getInt("quantity");
            Integer product_id= a.getInt("product_id");
            String imageUrl = BuildConfig.API_URL + '/' + a.getJSONObject("product").getString("image");
            Integer max_quantity = a.getJSONObject("product").getInt("max_quantity");
            Integer min_quantity = a.getJSONObject("product").getInt("min_quantity");
            holder.productName.setText(name);
            holder.productQuantity.setText(quantity.toString());
            holder.productPrice.setText(price.toString());
            Picasso.with(context).load(Uri.parse(imageUrl)).into(holder.productImage);
            holder.subscribeAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.addtoCart(holder.itemView,position,Integer.parseInt(holder.productQuantity.getText().toString()), price,product_id);
                }
            });
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(holder.productQuantity.getText().toString()) < max_quantity && Integer.parseInt(holder.productQuantity.getText().toString()) >= min_quantity) {
                        holder.productQuantity.setText(Integer.toString(Integer.parseInt(holder.productQuantity.getText().toString()) + 1));
                        cartInterface.updateSubscription(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
                    } else {
                        Toast.makeText(context, "Maximum quantity limit ", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(holder.productQuantity.getText().toString()) > min_quantity) {
                        holder.productQuantity.setText(Integer.toString(Integer.parseInt(holder.productQuantity.getText().toString()) - 1));
                        cartInterface.updateSubscription(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
                    } else {
                        Toast.makeText(context, "Mininum quantity limit", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.deleteSubscription(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
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

    public class CartHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView productImage;
        TextView productName, productPrice;
        EditText productQuantity;
        ImageButton addBtn, minusBtn, deleteBtn;
        Button subscribeAddBtn;
        public ItemClickListener listener;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.subscribe_image);
            productName = itemView.findViewById(R.id.subscribe_pname);
            productPrice = itemView.findViewById(R.id.subscribe_pprice);
            productQuantity = itemView.findViewById(R.id.subscribe_quantity);
            deleteBtn = itemView.findViewById(R.id.subscriptionDeleteBtn);
            addBtn = itemView.findViewById(R.id.addBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            subscribeAddBtn= itemView.findViewById(R.id.subscriptionAddBtn);
        }

        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}