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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDeviceCateActivity extends AppCompatActivity {
    TextView tRoomName;

    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private DeviceCategoryAdapter adapter;
    private List<DeviceCategory> deviceCategoryList = new ArrayList<>();
    public String locationID ;
    public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device);
        //tRoomName.findViewById(R.id.txtRoomName);
        //sRoomName = getIntent().getStringExtra("roomName");

        // tRoomName.setText(sRoomName);
        locationID = getIntent().getStringExtra("roomId");
        Log.d("Id reveived", locationID);
        url = Constant.PICK_LOCATION + "/" + locationID + "/device_categories";
        Log.d("URL", url);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getCateDevices();
    }


    private void getCateDevices()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray DeviceCateArray = object.getJSONArray("data");
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < DeviceCateArray.length(); i++) {
                        JSONObject deviceCategoryObj = DeviceCateArray.getJSONObject(i);
                        String Name = deviceCategoryObj.getString("name");
                        String deviceCateId = deviceCategoryObj.getString("id");
                        DeviceCategory deviceCategory = new DeviceCategory(Name, deviceCateId);
                        deviceCategoryList.add(deviceCategory);
                        Log.d("location", Name); // log location
                        Log.d("ID", deviceCateId);
                    }
                    for (DeviceCategory deviceCategory : deviceCategoryList) {
                        Log.d("Device Cate info", "Device Category Name: " + deviceCategory.getName() + "\n");
                        Log.d("Device Cate info", "Device Category ID: " + deviceCategory.getId() + "\n");
                    }
                    //recyclerView.addItemDecoration(new ItemDecoration(10));
                    adapter = new DeviceCategoryAdapter(deviceCategoryList, deviceCategory -> {
                        Intent intent = new Intent(HomeDeviceCateActivity.this, RemoteActivity.class);
//                        intent.putExtra("roomName", deviceCategory.getName());
//                        intent.putExtra("roomId", deviceCategory.getId());
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
