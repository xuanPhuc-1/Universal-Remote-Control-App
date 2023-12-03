package com.example.iotapp;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResultLauncher;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


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

    private EditText txtMacAddress, txtLocation;
    private Bitmap bitmap = null;
    private static final  int GALLERY_CHANGE_POST = 3;
    private ProgressDialog dialog;
    private SharedPreferences preferences;
    private ImageButton btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_add);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnAddHub = findViewById(R.id.btnAddHub);
        imgAddHub = findViewById(R.id.imgAddHub);
        txtMacAddress = findViewById(R.id.txtMacAddress);
        txtLocation = findViewById(R.id.txtLocation);

        btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            scanCode();
        });
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

    }

    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
            //set the result to the txtMacAddress
            txtMacAddress.setText(result.getContents());
        }
    });
        

    private void post(){
        dialog.setMessage("Adding");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,Constant.ADD_HUB,response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddActivity.this,HomeActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
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
