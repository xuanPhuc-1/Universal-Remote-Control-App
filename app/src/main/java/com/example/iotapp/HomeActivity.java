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
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;
    private static final int GALLERY_ADD_POST = 2;
    private ProgressDialog dialog;
    private TextView currentLocation;
    private  TextView currentUserName;
    private float currentTemp;
    private float currentHumid;


    // set home activity làm fragment chính
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer,new HomeFragment(),HomeFragment.class.getSimpleName()).commit();
        init();
    }

    private void init() {
        navigationView = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            //startActivityForResult(i,GALLERY_ADD_POST);
        });
        getUserInfoLocation();
//
//        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//                switch (item.getItemId()){
//                    case R.id.item_home: {
//                        Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
//                        if (account!=null){
//                            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();
//                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
//                        }
//                        break;
//                    }
//
//                    case R.id.item_account: {
//                        Fragment account = fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName());
//                        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName())).commit();
//                        if (account!=null){
//                            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(AccountFragment.class.getSimpleName())).commit();
//                        }
//                        else {
//                            fragmentManager.beginTransaction().add(R.id.frameHomeContainer,new AccountFragment(),AccountFragment.class.getSimpleName()).commit();
//                        }
//                        break;
//                    }
//                }
//
//                return  true;
//            }
//        });
//
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            Intent i = new Intent(HomeActivity.this,AddActivity.class);
            i.setData(imgUri);
            startActivity(i);
        }
    }


    private void getUserInfoLocation() {
        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.PICK_LOCATION, response -> {
            //we get response if connection success
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("location");
                    //make shared preference user
                    SharedPreferences userPref = this.getApplicationContext().getSharedPreferences("location",  MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name", object.getString("name"));
                    editor.putInt("id", user.getInt("id"));
                    editor.apply();

                    //if success
                    Toast.makeText(this, "Get info successed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            },error -> {
                // error if connection not success
                error.printStackTrace();
                Toast.makeText(this, "Get info failed", Toast.LENGTH_SHORT).show();
        }){

        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}