package com.example.adapters;


import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.R;
import com.example.interfaces.ItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    Context context;
    JSONArray category;
    RecyclerView rlProduct;
    public CategoryAdapter(Context ct, JSONArray p) {
        context = ct;
        category = p;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_category, parent, false);
        return new CategoryHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            Log.d("From", a.toString());
            String name = a.getString("name");
            JSONArray productList = a.getJSONArray("products");
            ProductAdapter myAdapter = new ProductAdapter(context, productList);
            rlProduct.setAdapter(myAdapter);
            rlProduct.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            holder.name.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return category.length();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ItemClickListener listener;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.category_name);
            rlProduct= itemView.findViewById(R.id.rlProduct);
        }

        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}