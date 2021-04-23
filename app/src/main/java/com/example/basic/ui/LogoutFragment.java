package com.example.basic.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.basic.MainActivity;
import com.example.basic.R;

public class LogoutFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences settings = view.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        settings.edit().remove("token").commit();
        settings.edit().remove("user_info").commit();
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }
}