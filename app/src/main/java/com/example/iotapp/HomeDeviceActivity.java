package com.example.iotapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Adapters.DeviceAdapter;
import com.example.iotapp.Objects.Device;
import com.example.iotapp.Objects.Device;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDeviceActivity extends AppCompatActivity {
    TextView tRoomName;

    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> deviceList = new ArrayList<>();
    public String deviceCateID;
    public String url;
    private static final int GALLERY_ADD_POST = 2;
    private BottomNavigationView navigationView;
    private FloatingActionButton add_cate_fab;
    private String deviceCateName = "";
    private String MAC = "";
    private String ir_codes = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.layout_device);
        if (getIntent().hasExtra("deviceCateID") && getIntent().hasExtra("deviceCateName")) {
            Log.d("Has extra", "Has extra");
            deviceCateID = getIntent().getStringExtra("deviceCateID");
            deviceCateName = getIntent().getStringExtra("deviceCateName");
            Log.d("Id reveived", deviceCateID);
            Log.d("Name reveived", deviceCateName);
            url = Constant.HOME + "/device_categories/" + deviceCateID + "/devices";
            Log.d("URL", url);
        } else {
            Log.d("No extra", "No extra");
        }
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.bottom_nav);
        add_cate_fab = findViewById(R.id.fab);

        add_cate_fab.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            //send deviceCategory name to AddDeviceActivity
            i.putExtra("deviceCateName", deviceCateName);
            startActivityForResult(i, GALLERY_ADD_POST);
        });
        navigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_home) {
                Intent intent = new Intent(HomeDeviceActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return false;
        });
        dialog = new ProgressDialog(this);

        getDevices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeDeviceActivity.this, AddDeviceActivity.class);
            i.setData(imgUri);
            //send deviceCategory name to AddDeviceActivity
            i.putExtra("deviceCateName", deviceCateName);
            //send deviceCategories to AddCateActivity
            startActivity(i);
        }
    }
    private void getDevices()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray DeviceArray = object.getJSONArray("data");
                    //get MAC address
                    MAC = object.getString("MAC");
                    //Log.d("MAC", MAC);
                    ir_codes = object.getString("ir_codes");
                    //Log.d("IR codes", ir_codes);
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < DeviceArray.length(); i++) {
                        JSONObject DeviceObj = DeviceArray.getJSONObject(i);
                        String Name = DeviceObj.getString("name");
                        String deviceId = DeviceObj.getString("id");
                        Device Device = new Device(Name, deviceId);
                        deviceList.add(Device);
                    }
                    //recyclerView.addItemDecoration(new ItemDecoration(10));
                    adapter = new DeviceAdapter(deviceList, Device -> {
                        Intent intent = new Intent(HomeDeviceActivity.this, RemoteActivity.class);
                        intent.putExtra("MAC", MAC);
                        intent.putExtra("ir_codes", ir_codes);
                        startActivity(intent);
                    });
                    recyclerView = findViewById(R.id.recyclerView); // tìm recyclerview
                    recyclerView.setAdapter(adapter); // set adapter cho recyclerview
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
            //userLocation.setText("error");
        }) {
            // add token to header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        queue.add(stringRequest);
    }
}
