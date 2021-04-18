package com.example.adapters;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorHolder> {
    Context context;
    JSONArray category;

    public DoctorAdapter(Context ct, JSONArray p) {
        context = ct;
        category = p;
    }

    @NonNull
    @Override
    public DoctorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.doctor_rows, parent, false);
        return new DoctorHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DoctorHolder holder, int position) {
        try {
            JSONObject a = (JSONObject) category.get(position);
            Log.d("From", a.toString());
            String name = a.getString("name");
           String imageUrl = null;
            if(!a.getString("image").isEmpty())
            {
                imageUrl= BuildConfig.API_URL+"/"+a.getString("image");
            }
            String post = a.getString("description");
            String phone = a.getString("phone");
            String address= a.getString("address");
            String hospital= a.getString("hospital");
            holder.name.setText("Name: "+ name);
            holder.post.setText("Post: "+post);
            holder.phone.setText("Phone: "+phone);
            holder.address.setText("Address: "+address);
            holder.hospital.setText("Hospital: "+hospital);
            Picasso.with(context).load(Uri.parse(imageUrl)).into(holder.doctorImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return category.length();
    }

    public class DoctorHolder extends RecyclerView.ViewHolder {
        TextView name,post,hospital,address,phone;
        ImageView doctorImg;
        public ItemClickListener listener;

        public DoctorHolder(@NonNull View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.doctor_name);
            post= itemView.findViewById(R.id.doctor_post);


        }
        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}