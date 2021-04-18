
package com.example.basic.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.adapters.OrderAdapter;
import com.example.basic.R;
import com.example.interfaces.OrderInterface;
import com.example.models.ApiError;
import com.example.models.ErrorUtils;
import com.example.network.Network;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.basic.R.id.shipping_address;

public class CheckoutFragment extends Fragment {
    JSONArray orderData = new JSONArray();
    ViewGroup con;
    Bundle bundle;
    TextView grossAmount, shippingCharge, totalAmount;
    EditText shippingAddress, billingAddress;
    ImageView imageView;
    Button submitBtn;
    int PICK_IMAGE = 1;
    ImageButton getLocation;
    File file = null;
    private int STORAGE_PERMISSION_CODE = 10;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_checkout, container, false);
        bundle = this.getArguments();
        shippingAddress = root.findViewById(R.id.checkout_shipping_address);
        billingAddress = root.findViewById(R.id.checkout_billing_address);
        shippingCharge = root.findViewById(R.id.shipping_charge);
        grossAmount = root.findViewById(R.id.gross_amount);
        totalAmount = root.findViewById(R.id.total_amount);
        submitBtn = root.findViewById(R.id.submitBtn);
        imageView = root.findViewById(R.id.prescription);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Granted permission", Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
        return root;
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public void orderCheckout() {
        if (bundle != null) {
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("orders"));
                Integer id = jsonObject.getInt("id");
                Retrofit retrofit = new Network().getRetrofit1();
                OrderInterface jsonPlaceholder = retrofit.create(OrderInterface.class);
                Map<String, RequestBody> map = new HashMap<>();
                JSONObject address = new JSONObject();
                address.put("shipping", shippingAddress.getText().toString());
                address.put("billing", billingAddress.getText().toString());
                address.put("order_id", id);
                map.put("id", toRequestBody(id.toString()));
                map.put("address", toRequestBody(address.toString()));
                map.put("shipping_charge", toRequestBody(shippingCharge.getText().toString()));
                map.put("total_amount", toRequestBody(totalAmount.getText().toString()));
                if (file != null) {
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
                    map.put("image\"; filename=\"pp.png\"", fileBody);
                }
                Call<ResponseBody> call = jsonPlaceholder.checkout(map);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String re = response.body().string();
                                Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_LONG).show();
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
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Created", "Home fragment");
        Log.d("Bundle", bundle.toString());

        if (bundle != null) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(bundle.getString("orders"));
                Integer id = jsonObject.getInt("id");
                Double shipping_charge = 30.0;
                Double total_amount = jsonObject.getDouble("gross_amount") + shipping_charge;
                Double gross_amount = jsonObject.getDouble("gross_amount");
                shippingCharge.setText(shipping_charge.toString());
                grossAmount.setText(gross_amount.toString());
                totalAmount.setText(total_amount.toString());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("Request code", Integer.toString(requestCode));
            Log.d("Result code", Integer.toString(resultCode));

            if (requestCode == PICK_IMAGE && resultCode == -1 && data != null) {
                Log.d("Request code", Integer.toString(requestCode));
                Log.d("Result code", Integer.toString(resultCode));
                Uri path = data.getData();
                file = new File(new FileUtils().getPath(path,
                        getContext()));
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(path).into(imageView);
//                Log.d("Image path", imagePath);

            }
        } catch (RuntimeException err) {

            Log.d("Err", err.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(getActivity(), R.id.checkout_shipping_address, RegexTemplate.NOT_EMPTY, R.string.required);
        awesomeValidation.addValidation(getActivity(), R.id.checkout_billing_address, RegexTemplate.NOT_EMPTY, R.string.required);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (awesomeValidation.validate()) {
                    orderCheckout();
                }
            }
        });
    }

}