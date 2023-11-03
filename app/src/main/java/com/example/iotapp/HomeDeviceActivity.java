package com.example.iotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Adapters.DeviceAdapter;
import com.example.iotapp.Objects.Device;
import com.example.iotapp.Objects.Device;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device);
        //tRoomName.findViewById(R.id.txtRoomName);
        //sRoomName = getIntent().getStringExtra("roomName");

        // tRoomName.setText(sRoomName);
        deviceCateID = getIntent().getStringExtra("deviceCateID");
        Log.d("Id reveived", deviceCateID);
        url = Constant.HOME + "/device_categories/" + deviceCateID + "/devices";
        Log.d("URL", url);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getDevices();
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
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < DeviceArray.length(); i++) {
                        JSONObject DeviceObj = DeviceArray.getJSONObject(i);
                        String Name = DeviceObj.getString("name");
                        String deviceId = DeviceObj.getString("id");
                        Device Device = new Device(Name, deviceId);
                        deviceList.add(Device);
                        Log.d("location", Name); // log location
                        Log.d("ID", deviceId);
                    }
                    for (Device Device : deviceList) {
                        Log.d("Device Cate info", "Device Category Name: " + Device.getName() + "\n");
                        Log.d("Device Cate info", "Device Category ID: " + Device.getId() + "\n");
                    }
                    //recyclerView.addItemDecoration(new ItemDecoration(10));
                    adapter = new DeviceAdapter(deviceList, Device -> {
                        Intent intent = new Intent(HomeDeviceActivity.this, RemoteActivity.class);
//                        intent.putExtra("roomName", Device.getName());
//                        intent.putExtra("roomId", Device.getId());
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
