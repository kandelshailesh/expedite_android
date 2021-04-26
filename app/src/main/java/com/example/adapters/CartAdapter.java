package com.example.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.BuildConfig;
import com.example.basic.R;
import com.example.basic.ui.MainCartFragment;
import com.example.interfaces.CartInterface;
import com.example.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    Context context;
    JSONObject category;
    MainCartFragment cartInterface;

    public CartAdapter(Context ct, JSONObject p, MainCartFragment f) {
        context = ct;
        category = p;
        cartInterface = f;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_cart_rows, parent, false);
        return new CartHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.getJSONArray("orders_items").get(position);
            Log.d("From", a.toString());
            String name = a.getJSONObject("product").getString("name");
            Double price = a.getJSONObject("product").getDouble("unit_price");
            Integer quantity = a.getInt("quantity");
            String imageUrl = BuildConfig.API_URL + '/' + a.getJSONObject("product").getString("image");
            Integer max_quantity = a.getJSONObject("product").getInt("max_quantity");
            Integer min_quantity = a.getJSONObject("product").getInt("min_quantity");
            double final_price=0.0;
            Boolean discountable= a.getJSONObject("product").getBoolean("discountable");
            Double discount_percent=0.0;
            if(discountable) {
                String discount_type = a.getJSONObject("product").getString("discount_type");
                Double discount_amount= a.getJSONObject("product").getDouble("discount_amount");
                Log.d("Discount",discount_type);
                if(discount_type.equals("%")) {
                    final_price=price-(0.01*discount_amount *price);
                }
                else
                {
                    final_price=price-discount_amount;
                }
                discount_percent=((price-final_price)/price)*100;
            }
            else
            {
                final_price=price;
            }
            holder.productName.setText(name);
            holder.productQuantity.setText(quantity.toString());
            holder.productPrice.setText("Rs."+Double.toString(final_price));
            Picasso.with(context).load(Uri.parse(imageUrl)).into(holder.productImage);
            if(discountable) {
                holder.price1.setText(Html.fromHtml("<del>Rs." + price + "</del>"));
                holder.discount1.setText("-"+discount_percent.toString() + '%');
            }
            else
            {
                holder.price1.setVisibility(holder.itemView.getRootView().GONE);
                holder.discount1.setVisibility(holder.itemView.getRootView().GONE);
            }
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(holder.productQuantity.getText().toString()) < max_quantity && Integer.parseInt(holder.productQuantity.getText().toString()) >= min_quantity) {
                        holder.productQuantity.setText(Integer.toString(Integer.parseInt(holder.productQuantity.getText().toString()) + 1));
                        cartInterface.addtoCart(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
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
                        cartInterface.addtoCart(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
                    } else {
                        Toast.makeText(context, "Mininum quantity limit", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartInterface.deletefromCart(holder.itemView, position, Integer.parseInt(holder.productQuantity.getText().toString()), price);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        try {
            if (category.getJSONArray("orders_items") != null) {
                count = category.getJSONArray("orders_items").length();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

    public class CartHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView productImage;
        TextView productName, productPrice, price1, discount1;
        EditText productQuantity;
        ImageView addBtn, minusBtn, deleteBtn;
        public ItemClickListener listener;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.cart_image);
            productName = itemView.findViewById(R.id.cart_pname);
            productPrice = itemView.findViewById(R.id.cart_pprice);
            productQuantity = itemView.findViewById(R.id.cart_quantity);
            deleteBtn = itemView.findViewById(R.id.cart_delete);
            addBtn = itemView.findViewById(R.id.addBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            price1 = itemView.findViewById(R.id.product_cost1);
            discount1 = itemView.findViewById(R.id.product_discount1);
        }

        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}