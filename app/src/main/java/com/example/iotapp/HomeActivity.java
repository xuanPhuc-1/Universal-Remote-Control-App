package com.example.iotapp;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private RequestQueue requestQueue;
    private TextView currentUserName;
    private float currentTemp;
    private float currentHumid;

    private SharedPreferences preferences;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList = new ArrayList<>();


    // set home activity làm fragment chính
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        init();
//        fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer, new HomeFragment(), HomeFragment.class.getSimpleName()).commit();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        navigationView = findViewById(R.id.bottom_nav);
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
                    JSONArray locationsArray = object.getJSONArray("locations");
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        String Name = location.getString("name");
                        Location locationObj = new Location(Name);
                        locationList.add(locationObj);
                        Log.d("location", Name); // log location
                    }
                    for (Location location : locationList) {
                        Log.d("LocationInfo", "Name: " + location.getName() + "\n");
                    }
                    adapter = new LocationAdapter(locationList); // tạo adapter cho recyclerview
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
    private void getCurrentTempAndHumid() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_TEMP_HUMID, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray locationsArray = object.getJSONArray("locations");
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        String sensor = location.getString("sensor");
                        //locationNames.add(sensor);
                    }
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
    private void deviceCategory() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_DEVICES_CATEGORY, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray locationsArray = object.getJSONArray("locations");
                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        String device = location.getString("data");
                        //locationNames.add(device);
                    }
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

}