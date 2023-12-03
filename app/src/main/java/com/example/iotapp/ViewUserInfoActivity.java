package com.example.iotapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserInfoActivity extends AppCompatActivity {

    private SharedPreferences userPref;
    private ProgressDialog dialog;

    private TextInputEditText txtName,txtEmail,txtPassword;

    private Button btnLogout;
    private CircleImageView circleImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.layout_user_infor);
        init();
        viewUserInfo();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        txtEmail = findViewById(R.id.txtEmailUserInfo);
        txtName = findViewById(R.id.txtNameUserInfo);
        txtPassword = findViewById(R.id.txtPasswordUserInfo);
        btnLogout = findViewById(R.id.btnLogout);

        circleImageView = findViewById(R.id.imgUserInfo);
        btnLogout.setOnClickListener(v->{
            logout();
        });
    }

    private void logout(){
        dialog.setMessage("Logging out...");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,Constant.LOGOUT,response->{

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(ViewUserInfoActivity.this,AuthActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        },error ->{
            error.printStackTrace();
            dialog.dismiss();
        } ){

            //add token to headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ViewUserInfoActivity.this);
        queue.add(request);
    }

    private void viewUserInfo(){


        StringRequest request = new StringRequest(Request.Method.GET,Constant.GET_USER_INFO,response->{

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    String name = user.getString("name");
                    String email = user.getString("email");
                    String image = user.getString("photo");

                    txtName.setText(name);
                    txtEmail.setText(email);
                    txtPassword.setText("********");
                    if (!image.equals("null")){
                        Log.d("Path",Constant.URL+"/storage/profiles/"+image);
                        Picasso.get().load(Constant.URL+"/storage/profiles/"+image).into(circleImageView);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        },error ->{
            error.printStackTrace();
            dialog.dismiss();
        } ){

            //add token to headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ViewUserInfoActivity.this);
        queue.add(request);
    }

















}