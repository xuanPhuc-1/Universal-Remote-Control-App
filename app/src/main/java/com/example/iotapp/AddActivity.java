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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddActivity extends AppCompatActivity {
    private Button btnAddHub;
    private CircleImageView imgAddHub;

    private Button btnLogout;
    private EditText txtMacAddress, txtLocation;
    private Bitmap bitmap = null;
    private static final  int GALLERY_CHANGE_POST = 3;
    private ProgressDialog dialog;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddHub = findViewById(R.id.btnAddHub);
        imgAddHub = findViewById(R.id.imgAddHub);
        btnLogout = findViewById(R.id.btnLogout);
        txtMacAddress = findViewById(R.id.txtMacAddress);
        txtLocation = findViewById(R.id.txtLocation);


        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        imgAddHub.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnAddHub.setOnClickListener(v->{
            if(!txtMacAddress.getText().toString().isEmpty()){
                post();
            }else {
                Toast.makeText(this, "Mac address is required", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v->{
            //validate fields first
            logout();
        });
    }

            
        

    private void post(){
        dialog.setMessage("Adding");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,Constant.ADD_HUB,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){

//                    JSONObject hubObject = object.getJSONObject("hub");
//                    JSONObject userObject = object.getJSONObject("user");
//                    JSONObject locationObject = object.getJSONObject("location");
//
//                    User user = new User();
//                    user.setId(userObject.getInt("id"));
//                    user.setUserName(userObject.getString("name"));
//                    user.setPhoto(userObject.getString("photo"));
//
//                    Hub hub = new Hub();
//                    hub.setUser(user);
//                    hub.setId(hubObject.getInt("id"));
//                    hub.setMAC(hubObject.getString("mac"));
//                    hub.setDate(hubObject.getString("created_at"));

//                    Location location = new Location();
//                    location.setUser(user);
//                    location.setId(locationObject.getInt("id"));
//                    location.setName(locationObject.getString("name"));

                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
//                    finish();
                    //return to home activity
                    startActivity(new Intent(AddActivity.this,HomeActivity.class));
                    finish();
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
                map.put("mac",txtMacAddress.getText().toString().trim());
                map.put("location_name",txtLocation.getText().toString().trim());
                map.put("photo",bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddActivity.this);
        queue.add(request);

    }
    private void logout(){

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
        startActivity(new Intent(AddActivity.this,AuthActivity.class));
        finish();

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
            imgAddHub.setImageURI(imgUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
