package com.example.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.BuildConfig;
import com.example.basic.R;
import com.example.basic.ui.ProductCartFragment;
import com.example.interfaces.ItemClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    Context context;
    JSONArray category;

    public ProductAdapter(Context ct, JSONArray p) {
        context = ct;
        category = p;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_product, parent, false);
        return new ProductHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            Log.d("From", a.toString());
            String name = a.getString("name");
           String imageUrl = null;
            if(!a.getString("image").isEmpty())
            {
                imageUrl= BuildConfig.API_URL+"/"+a.getString("image");
            }
            double price= a.getDouble("unit_price");
            double final_price=0.0;
            Boolean discountable= a.getBoolean("discountable");
            Double discount_percent=0.0;
            if(discountable) {
                String discount_type = a.getString("discount_type");
                Double discount_amount= a.getDouble("discount_amount");
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
            String description = a.getString("description");
            Integer min_quantity= a.getInt("min_quantity");
            Integer max_quantity= a.getInt("max_quantity");
            Integer product_id= a.getInt("id");
            holder.name.setText(name);
            holder.price.setText("Rs "+final_price);
            if(discountable) {
                holder.price1.setText(Html.fromHtml("<del>Rs." + price + "</del>"));
                holder.discount1.setText("-"+discount_percent.toString() + '%');
            }
            else
            {
                holder.price1.setVisibility(holder.itemView.getRootView().GONE);
                holder.discount1.setVisibility(holder.itemView.getRootView().GONE);
            }
            Picasso.with(context).load(Uri.parse(imageUrl)).into(holder.productImg);
            String finalImageUrl = imageUrl;
            double final_price1 = final_price;
            Double finalDiscount_percent = discount_percent;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductCartFragment blogDetailFragment = new ProductCartFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("description", description);
                    bundle.putString("imageUrl", finalImageUrl);
                    bundle.putString("name", name);
                    bundle.putString("price",Double.toString(final_price1));
                    bundle.putString("unit_price",Double.toString(price));
                    bundle.putDouble("discount_percent",finalDiscount_percent);
                    bundle.putInt("min_quantity", min_quantity);
                    bundle.putInt("max_quantity", max_quantity);
                    bundle.putInt("product_id",product_id);
                    blogDetailFragment.setArguments(bundle);
                    Navigation.findNavController(view).navigate(R.id.nav_item_cart,bundle);
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

    public class ProductHolder extends RecyclerView.ViewHolder {
        TextView name,price,price1,discount1;
        ImageView productImg;
        public ItemClickListener listener;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.product_name);
            price =  itemView.findViewById(R.id.product_cost);
            price1 =  itemView.findViewById(R.id.product_cost1);
            productImg = itemView.findViewById(R.id.product_image);
            discount1= itemView.findViewById(R.id.product_discount1);
        }
        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}