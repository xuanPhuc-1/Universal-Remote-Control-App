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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Adapters.DeviceCategoryAdapter;
import com.example.iotapp.Objects.Device;
import com.example.iotapp.Objects.DeviceCategory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeDeviceCateActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private ProgressDialog dialog;
    private FloatingActionButton add_cate_fab;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private DeviceCategoryAdapter adapter;
    private List<DeviceCategory> deviceCategoryList = new ArrayList<>();
    public String locationID, locationName,userName ;
    public String url;
//    private static final int GALLERY_ADD_POST = 2;

    private ArrayList<DeviceCategory> deviceCategories = new ArrayList<>();
    ArrayList<String> deviceCategoryNames = new ArrayList<>();
    private TextView txtRoomName,txtTempFromSensor,txtHumidFromSensor;

    private int humidity, temperature;
    private ArrayList<Device> devices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.layout_device_cate);
        for (DeviceCategory category : deviceCategories) {
            deviceCategoryNames.add(category.getName());
        }
        navigationView = findViewById(R.id.bottom_nav);
        locationID = getIntent().getStringExtra("roomId");
        locationName = getIntent().getStringExtra("roomName");
        userName = getIntent().getStringExtra("userName");
        //get the array sent from HomeActivity
        deviceCategories = getIntent().getParcelableArrayListExtra("deviceCategories");
        Log.d("Id reveived", locationID);
        url = Constant.PICK_LOCATION + "/" + locationID + "/device_categories";
        Log.d("URL", url);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.bottom_nav);
        add_cate_fab = findViewById(R.id.fab);
        txtRoomName = findViewById(R.id.txtRoomName);
        txtRoomName.setText(locationName);
        txtTempFromSensor = findViewById(R.id.txtTempFromSensor);
        txtHumidFromSensor = findViewById(R.id.txtHumidFromSensor);

        add_cate_fab.setOnClickListener(v -> {

            Intent i = new Intent(HomeDeviceCateActivity.this, AddCateActivity.class);
            //send locationID to AddCateActivity
            i.putExtra("roomName", locationName);
            //send deviceCategories to AddCateActivity
            i.putParcelableArrayListExtra("deviceCategories", deviceCategories);
            startActivity(i);
        });

        navigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_home) {
                Intent intent = new Intent(HomeDeviceCateActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return false;
        });

        getCateDevices();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK) {
//            Uri imgUri = data.getData();
//            Intent i = new Intent(HomeDeviceCateActivity.this, AddCateActivity.class);
//            i.setData(imgUri);
//            //send locationID to AddCateActivity
//            i.putExtra("roomName", locationName);
//            //send deviceCategories to AddCateActivity
//            i.putParcelableArrayListExtra("deviceCategories", deviceCategories);
//            startActivity(i);
//        }
//    }

    private void getCateDevices()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray DeviceCateArray = object.getJSONArray("data");
                    JSONObject SensorObj = object.getJSONObject("sensor");
                    humidity = SensorObj.getInt("humidity");
                    //convert humidity to string
                    String humidityString = String.valueOf(humidity);
                    temperature = SensorObj.getInt("temperature");
                    //convert temperature to string
                    String temperatureString = String.valueOf(temperature);
                    txtHumidFromSensor.setText(humidityString + "%");
                    txtTempFromSensor.setText(temperatureString + "°C");
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
                    JSONArray devicesArray = object.getJSONArray("devices"); // Sử dụng optJSONArray thay vì getJSONArray
                    for (int i = 0; i < devicesArray.length(); i++) {
                        JSONObject deviceObject = devicesArray.getJSONObject(i);
                        String id = deviceObject.getString("id");
                        String name = deviceObject.getString("name");
                        String deviceCateID = deviceObject.getString("device_category_id");
                        String ir_codes = deviceObject.getString("ir_codes");
                        Device device = new Device(name, id, deviceCateID, ir_codes);
                        devices.add(device);
                    }
                    for (DeviceCategory deviceCategory : deviceCategoryList) {
                        Log.d("Device Cate info", "Device Category Name: " + deviceCategory.getName() + "\n");
                        Log.d("Device Cate info", "Device Category ID: " + deviceCategory.getId() + "\n");
                    }
                    //recyclerView.addItemDecoration(new ItemDecoration(10));
                    adapter = new DeviceCategoryAdapter(deviceCategoryList, deviceCategory -> {
                        Intent intent = new Intent(HomeDeviceCateActivity.this, HomeDeviceActivity.class);
                        intent.putExtra("deviceCateName", deviceCategory.getName());
                        intent.putExtra("deviceCateID", deviceCategory.getId());
                        //send device string array to HomeDeviceActivity
                        intent.putExtra("devices", devices);
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
