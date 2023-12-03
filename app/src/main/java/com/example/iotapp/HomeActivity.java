package com.example.iotapp;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Objects.DeviceCategory;
import com.example.iotapp.Objects.Location;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import com.example.iotapp.Adapters.LocationAdapter;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private SharedPreferences preferences;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList = new ArrayList<>();
    //declare a string array to store all device category name
    private ArrayList<DeviceCategory> deviceCategories = new ArrayList<>();
    private TextView txtUserName,txtLabelRoom;
    private TextView txtTemperature;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.layout_home);
        txtTemperature = findViewById(R.id.txtTemp);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initLocationCallback();
        requestLocationUpdates();
        init();

    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Lấy vị trí hiện tại
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                getWeatherInfo(latitude, longitude);
            }
        };
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        // Kiểm tra quyền truy cập vị trí
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void getWeatherInfo(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        String apiKey = "a63c667d1b4dfc87ac845b3dc4e3d2da"; // Thay thế bằng API key của bạn

        Call<WeatherResponse> call = weatherApi.getWeather(latitude, longitude, apiKey, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        double temperature = weatherResponse.getMain().getTemperature();
                        // Xử lý thông tin thời tiết, ví dụ:
//                        txtTemperature.setText(String.valueOf(temperature) + "°C");
                        Log.d("Weather", "onResponse: " + temperature);
                    }
                } else {
                    // Xử lý khi có lỗi từ API
                    Log.e("Weather", "Failed to get weather data");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Xử lý khi có lỗi kết nối
                Log.e("Weather", "Failed to connect to OpenWeatherMap API");
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.bottom_nav);
        txtUserName = findViewById(R.id.txtUserName);
        txtLabelRoom = findViewById(R.id.txtLabelRoom);
        navigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_account) {
                Intent intent = new Intent(HomeActivity.this, ViewUserInfoActivity.class);
                startActivity(intent);
            }
            return false;
        });


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_ADD_POST);
        });
        getUserLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this, AddActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }


    private void getUserLocation() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.PICK_LOCATION, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    //check locations object is empty, set text txtLabelRoom to "No location"
                    if(object.getJSONArray("locations").length() == 0)
                    {
                        txtLabelRoom.setText("No location. Please add one!");
                    }
                    JSONArray locationsArray = object.getJSONArray("locations");
                    //get user name
                    JSONObject user = object.getJSONObject("user");
                    String userName = user.getString("name");
                    txtUserName.setText(userName);

                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        String Name = location.getString("name");
                        String locationId = location.getString("id");
                        Location locationObj = new Location(Name, locationId);
                        locationList.add(locationObj);
                        Log.d("location", Name); // log location
                        Log.d("ID", locationId);
                    }
                    for (Location location : locationList) {
                        Log.d("LocationInfo", "Name: " + location.getName() + "\n");
                    }
                    JSONArray deviceCategoriesArray = object.getJSONArray("deviceCategories"); // Sử dụng optJSONArray thay vì getJSONArray
                    for (int i = 0; i < deviceCategoriesArray.length(); i++) {
                        JSONObject categoryObject = deviceCategoriesArray.getJSONObject(i);
                        String id = categoryObject.getString("id");
                        String name = categoryObject.getString("name");
                        String photo = categoryObject.getString("photo");
                        DeviceCategory deviceCategory = new DeviceCategory(name, id, photo);
                        deviceCategories.add(deviceCategory);
                    }
                    //recyclerView.addItemDecoration(new ItemDecoration(10));
                    adapter = new LocationAdapter(locationList, location -> {
                        Intent intent = new Intent(HomeActivity.this, HomeDeviceCateActivity.class);
                        intent.putExtra("roomName", location.getName());
                        intent.putExtra("roomId", location.getId());
                        intent.putExtra("userName", userName);
                        //send device category string array to HomeDeviceCateActivity
                        intent.putExtra("deviceCategories", deviceCategories);
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