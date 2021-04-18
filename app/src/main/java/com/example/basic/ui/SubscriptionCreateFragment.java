
package com.example.basic.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.adapters.SubscriptionAdapter;
import com.example.basic.R;
import com.example.interfaces.OrderInterface;
import com.example.interfaces.SubscriptionInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubscriptionCreateFragment extends Fragment {
    EditText quantity,name;
    Integer user_id;
    Button submitBtn;
    Bundle bundle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_subscription, container, false);
        quantity = root.findViewById(R.id.product_quantity);
        submitBtn = root.findViewById(R.id.submitBtn);
        name=root.findViewById(R.id.product_name);
        bundle = this.getArguments();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        if(bundle!=null) {
            name.setText(bundle.getString("name"));
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSubscription();
                }
            });
        }
    }


    public void addSubscription() {
        try {
            JSONObject a = new JSONObject();
            Integer quantity1 = Integer.parseInt(quantity.getText().toString());
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            String user_info = sharedPreferences.getString("user_info", "");
            if (user_info.isEmpty()) {
                Log.d("User", "Not found");
            } else {
                try {
                    JSONObject s = new JSONObject(user_info);
                    user_id = s.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            a.put("quantity", quantity1);
            a.put("user_id", user_id);
            a.put("product_id", bundle.getInt("id"));
            Retrofit retrofit = new Network().getRetrofit1();
            SubscriptionInterface jsonPlaceholder = retrofit.create(SubscriptionInterface.class);
            Call<ResponseBody> call = jsonPlaceholder.create(a.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    submitBtn.setEnabled(true);
                    submitBtn.setAlpha(1);
                    if (response.isSuccessful()) {
                        try {
                            String re = response.body().string();
                            Log.d("Order", re);
                            Toast.makeText(getContext(), "Item subscribed successfully", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(getView()).navigate(R.id.nav_home);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ApiError error = ErrorUtils.parseError(retrofit, response);
                        Log.d("Error", error.getError());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    submitBtn.setEnabled(true);
                    submitBtn.setAlpha(1);
                }
            });
        } catch (JSONException e) {
            Log.d("dgdg", e.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.product_quantity, RegexTemplate.NOT_EMPTY, R.string.required);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(awesomeValidation.validate())
                {
                    submitBtn.setEnabled(false);
                    submitBtn.setAlpha(0.5f);
                    addSubscription();
                }
            }
        });

    }
}