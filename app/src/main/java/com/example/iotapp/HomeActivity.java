package com.example.iotapp;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import java.util.Map;


public class HomeActivity extends AppCompatActivity {
    public ArrayList<String> locationNames = new ArrayList<>();

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private RequestQueue requestQueue;
    private TextView currentLocation;
    private TextView currentUserName;
    private float currentTemp;
    private float currentHumid;

    private TextView userLocation;
    private SharedPreferences preferences;
    private ProgressDialog dialog;


    // set home activity làm fragment chính
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer, new HomeFragment(), HomeFragment.class.getSimpleName()).commit();
        init();
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
        userLocation = findViewById(R.id.txtLocation);

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

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.PICK_LOCATION, response ->
        {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray locationsArray = object.getJSONArray("locations");

                    // Lặp qua tất cả các phần tử trong mảng "locations"
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        String name = location.getString("name");
                        //"locations" là một mảng JSON chứa một đối tượng JSON.
                        // Vì vậy, bạn có thể sử dụng phương thức getJSONArray("locations")
                        // để truy cập mảng này và sau đó lấy đối tượng JSON đầu tiên từ mảng đó.
                        // Dưới đây là mã để trích xuất thông tin "name" từ đối tượng JSON "Phòng khách":
                        locationNames.add(name);
                        // Tạo một ListView trong layout XML của bạn (ví dụ: activity_main.xml)
                        //ListView listView = findViewById(R.id.listView);
                        //
                        //// Tạo một ArrayAdapter để hiển thị danh sách tên vị trí trên ListView
                        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationNames);
                        //
                        //// Đặt adapter cho ListView
                        //listView.setAdapter(adapter);
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

            // add token to header
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