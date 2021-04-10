package com.example.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basic.R;
import com.example.interfaces.ItemClickListener;
import com.example.models.posts.Post;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PostHolder>  {
  Context context;
  List<Post> posts;
   public  MyAdapter(Context ct,List<Post> p)
   {
   context=ct;
   posts=p;
   }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view= inflater.inflate(R.layout.activity_rows,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
       holder.ID.setText(Integer.toString(posts.get(position).getId()));
holder.user_id.setText(Integer.toString(posts.get(position).getUserId()));
holder.content.setText(posts.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public class PostHolder extends RecyclerView.ViewHolder {
       TextView ID, user_id, content;
        public ItemClickListener listener;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            ID = (TextView) itemView.findViewById(R.id.ID);
            user_id = (TextView) itemView.findViewById(R.id.user_id);
            content = (TextView) itemView.findViewById(R.id.content);
        }

        public void setItemClickListner(ItemClickListener listener) {
            this.listener = listener;
        }

        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition(), false);
        }
    }
}