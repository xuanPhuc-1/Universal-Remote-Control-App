package com.example.iotapp;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private RequestQueue requestQueue;
    private TextView currentLocation;
    private TextView currentUserName;
    private float currentTemp;
    private float currentHumid;


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
        navigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_ADD_POST);
        });
        currentLocation = findViewById(R.id.txtLocation);
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
//        requestQueue = Volley.newRequestQueue(this);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Constant.PICK_LOCATION, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("locations");
//                            JSONObject jsonObject = jsonArray.getJSONObject(1);
//                            String nameLocation = jsonObject.getString("name");
//                            currentLocation.setText(nameLocation);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                currentLocation.setText("Error input location");
//            }
//        });
//        requestQueue.add(request);
//    }

        StringRequest request = new StringRequest(Request.Method.GET, Constant.PICK_LOCATION, response -> {
            //we get response if connection success
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("locations");
                    //make shared preference user
                    SharedPreferences userPref = getSharedPreferences("locations", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name", user.getString("name"));
                    editor.apply();
                    String nameLocation = user.getString("name");
                    currentLocation.setText(nameLocation);

                    //if success
                    Toast.makeText(this, "Get info successed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // error if connection not success
            error.printStackTrace();
            currentLocation.setText("Nowhere");
            Toast.makeText(this, "Get info failed", Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}