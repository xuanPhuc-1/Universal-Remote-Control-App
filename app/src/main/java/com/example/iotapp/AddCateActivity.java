package com.example.iotapp;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.iotapp.Objects.DeviceCategory;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCateActivity extends AppCompatActivity {

    private Bitmap bitmap = null;
    private TextView txtRoomName ;
    private static final  int GALLERY_CHANGE_POST = 3;
    private CircleImageView imgAddCategory;
    private Button btnAddCategory;

    //create a array contain all room name. send to spinner
    private Spinner spinnerDeviceCategory;
    //list of room name get from

    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private ArrayList<DeviceCategory> deviceCategories = new ArrayList<>();
    ArrayList<String> deviceCategoryNames = new ArrayList<>();

    //private ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deviceCategoryNames);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_cate);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        imgAddCategory = findViewById(R.id.imgAddCategory);
        imgAddCategory.setImageURI(getIntent().getData());
        btnAddCategory = findViewById(R.id.btnAddCategory);
        spinnerDeviceCategory = findViewById(R.id.spinnerDeviceCategory);
        deviceCategories = getIntent().getParcelableArrayListExtra("deviceCategories");
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinnerDeviceCategory.setAdapter(adapter);

        if (deviceCategories != null) {
            //print out the array
            for (DeviceCategory deviceCategory : deviceCategories) {
                Log.d("Device Cate info", "Device Category Name: " + deviceCategory.getName() + "\n");
                Log.d("Device Cate info", "Device Category ID: " + deviceCategory.getId() + "\n");
            }
            for (DeviceCategory category : deviceCategories) {
                deviceCategoryNames.add(category.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deviceCategoryNames);
            spinnerDeviceCategory.setAdapter(adapter);
        }
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        txtRoomName = findViewById(R.id.txtRoomName);
        txtRoomName.setText(getIntent().getStringExtra("roomName"));
        btnAddCategory.setOnClickListener(v->{
            post();
        });


    }
    private void post(){
        dialog.setMessage("Adding");
        dialog.show();

//        StringRequest request = new StringRequest(Request.Method.POST,Constant.ADD_HUB,response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")){
//                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AddCateActivity.this,HomeActivity.class));
//                    finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            dialog.dismiss();
//
//        },error -> {
//            error.printStackTrace();
//            dialog.dismiss();
//        }){
//
//            // add token to header
//
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = preferences.getString("token","");
//                HashMap<String,String> map = new HashMap<>();
//                map.put("Authorization","Bearer "+token);
//                return map;
//            }
//
//            // add params
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String,String> map = new HashMap<>();
//                map.put("mac",txtMacAddress.getText().toString().trim());
//                map.put("location_name",txtLocation.getText().toString().trim());
//                map.put("photo",bitmapToString(bitmap));
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(AddActivity.this);
//        queue.add(request);

    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array,Base64.DEFAULT);
        }

        return "";
    }


    public void cancelPost(View view) {
        super.onBackPressed();
    }

    public void changePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,GALLERY_CHANGE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_CHANGE_POST && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgAddCategory.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
