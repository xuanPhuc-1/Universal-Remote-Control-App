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
import android.view.WindowManager;
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
import com.google.android.material.textfield.TextInputEditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddDeviceActivity extends AppCompatActivity {

    private Bitmap bitmap = null;

    private static final  int GALLERY_CHANGE_POST = 3;
    private CircleImageView imgAddDeviceName;

    private SharedPreferences preferences;
    private TextView txtDeviceCategoryName;
    private ProgressDialog dialog;
    private TextInputEditText txtDeviceName;
    private Button btnAddDeviceName;
    private String deviceCateName;




    //private ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, deviceCategoryNames);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.add_device_name);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        imgAddDeviceName = findViewById(R.id.imgAddDeviceName);
        imgAddDeviceName.setImageURI(getIntent().getData());
        txtDeviceCategoryName = findViewById(R.id.txtDeviceCategoryName);
        deviceCateName = getIntent().getStringExtra("deviceCateName");
        txtDeviceCategoryName.setText(deviceCateName);
        txtDeviceName = findViewById(R.id.txtDeviceName);
        btnAddDeviceName = findViewById(R.id.btnAddDeviceName);
        btnAddDeviceName.setOnClickListener(v->{
            Log.d("Device name", txtDeviceName.getText().toString().trim());
            Log.d("Device cate name", deviceCateName);
            Toast.makeText(this, "Device name added successfully", Toast.LENGTH_SHORT).show();
            post();
        });

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void post(){
        dialog.setMessage("Adding");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,Constant.CREATE_DEVICE,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Toast.makeText(this, "Device added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDeviceActivity.this,HomeActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(this, "Device name already exists", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){

            // add token to header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            // add params

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("device_category",deviceCateName);
                //send device category name of the selected item in spinner
                map.put("device_name",txtDeviceName.getText().toString().trim());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddDeviceActivity.this);
        queue.add(request);

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
            imgAddDeviceName.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
