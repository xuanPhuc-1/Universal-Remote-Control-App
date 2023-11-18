package com.example.iotapp;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Objects.Device;
import com.example.iotapp.Objects.DeviceCategory;
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDeviceActivity extends AppCompatActivity {


    private SharedPreferences preferences;
    private TextView txtDeviceCategoryName;
    private ProgressDialog dialog;
    private Spinner spinnerDeviceName;
    private Button btnAddDeviceName;
    private String deviceCateName;
    private String deviceName;

    private ArrayList<Device> devices = new ArrayList<>();
    ArrayList<String> deviceNames = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.add_device_name);

        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        txtDeviceCategoryName = findViewById(R.id.txtDeviceCategoryName);
        deviceCateName = getIntent().getStringExtra("deviceCateName");
        txtDeviceCategoryName.setText(deviceCateName);
        spinnerDeviceName = findViewById(R.id.spinnerDeviceName);
        btnAddDeviceName = findViewById(R.id.btnAddDeviceName);
        devices = getIntent().getParcelableArrayListExtra("deviceSupport");
        btnAddDeviceName.setOnClickListener(v->{

            //Log the url of ADD_DEVICE
            Log.d("URL", Constant.ADD_DEVICE);
            deviceName = spinnerDeviceName.getSelectedItem().toString().trim();
            //Log the type of deviceName
            Log.d("Device name", deviceName);
            post();
        });

        if (devices != null) {
            //print out the array
            for (Device deviceCategory : devices) {
                Log.d("Device Cate info", "Device Category Name: " + deviceCategory.getName() + "\n");
                Log.d("Device Cate info", "Device Category ID: " + deviceCategory.getId() + "\n");
            }
            for (Device category : devices) {
                deviceNames.add(category.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deviceNames);
            spinnerDeviceName.setAdapter(adapter);
        }
    }
    private void post() {
        dialog.setMessage("Adding");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_DEVICE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Toast.makeText(this, "Device added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDeviceActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Device name already exists", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            Toast.makeText(this, "Failed to add device", Toast.LENGTH_SHORT).show();
            //Return home activity
            startActivity(new Intent(AddDeviceActivity.this, HomeActivity.class));
            error.printStackTrace();
            dialog.dismiss();
        }) {

            // add token to header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                // send device category name of the selected item in spinner
                map.put("device_category_name", deviceCateName);
                map.put("device_name", deviceName);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddDeviceActivity.this);
        queue.add(request);
    }

    public void cancelPost(View view) {
        super.onBackPressed();
    }
}
