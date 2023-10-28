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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDeviceActivity extends AppCompatActivity {

    public String ID;
    TextView tRoomName;
    private String sRoomName;
    public String GET_TEMP_HUMID = "http://iotdomain.giize.com/locations/" + ID + "/get_info";
    public String GET_DEVICES_CATEGORY = "http://iotdomain.giize.com/locations/" + ID + "/device_categories";
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device);
        //tRoomName.findViewById(R.id.txtRoomName);

        ID = getIntent().getStringExtra("id");
        //sRoomName = getIntent().getStringExtra("roomName");

       // tRoomName.setText(sRoomName);

        Log.d("Id reveived", ID);
        //init();
    }

    private void init(){
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getDevice();
    }

    private void getDevice() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_DEVICES_CATEGORY, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray devicesArray = object.getJSONArray("data");
                    // Lặp qua tất cả các phần tử trong mảng "device"
                    for (int i = 0; i < devicesArray.length(); i++) {
                        JSONObject device = devicesArray.getJSONObject(i);
                        String Name = device.getString("name");
                        Device deviceObj = new Device(Name);
                        deviceList.add(deviceObj);
                        Log.d("device", Name); // log location
                    }
                    //adapter and recycleview
                    adapter = new DeviceAdapter(deviceList, device -> {
                        Intent intent = new Intent(HomeDeviceActivity.this, RemoteActivity.class);
                        startActivity(intent);
                    });
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
            // add params
        };
        queue.add(stringRequest);
    }

//    private void getCurrentTempAndHumid() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_TEMP_HUMID, response ->
//        {
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//                    JSONArray locationsArray = object.getJSONArray("locations");
//                    // Lặp qua tất cả các phần tử trong mảng "locations"
//                    for (int i = 0; i < locationsArray.length(); i++) {
//                        JSONObject location = locationsArray.getJSONObject(i);
//                        String sensor = location.getString("sensor");
//                        //locationNames.add(sensor);
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                dialog.dismiss();
//            }
//        }, error -> {
//            error.printStackTrace();
//            dialog.dismiss();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = preferences.getString("token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer " + token);
//                return map;
//            }
//            // add params
//        };
//        queue.add(stringRequest);
//    }
}