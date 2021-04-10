package com.example.models.posts;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Post {
    private int userId;
    private int id;
    private String title;
    @SerializedName("body")
    private String text;

    @NonNull
    @Override
    public String toString() {
        return "ID is"+id;
    }

    public Post()
    {

    }

    public Post(int id,int userId,String title,String text)
    {
        this.id= id;
        this.userId=userId;
        this.title=title;
        this.text=text;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
