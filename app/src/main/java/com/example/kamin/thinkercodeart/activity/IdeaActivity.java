package com.example.kamin.thinkercodeart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.MultipartRequest;
import com.example.kamin.thinkercodeart.volley.Singleton;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class IdeaActivity extends AppCompatActivity {
    final static public String TAG = StartActivity.class.getSimpleName();
    String name, body, tags, auth;
    Context context;
    EditText etName, etBody, etTags;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.CreateIdea);
        toolbar.setTitleMargin(0,0,0,0);

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        auth = sPref.getString(getResources().getString(R.string.AUTH), "");
        etName = (EditText) findViewById(R.id.etName);
        etBody = (EditText) findViewById(R.id.etBody);
        etTags = (EditText) findViewById(R.id.etTags);
        etName.setText("Name bla");
        etBody.setText("Body bla bla bla bla bla bla");
        etTags.setText("#bla #bla");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    void onClickCreate(View v){
        name = etName.getText().toString();
        body = etBody.getText().toString();
        tags = etTags.getText().toString();
        addRequest();
    }

    void onClickAddFile(View v){
        Intent intent = new Intent(this, FileManagerActivity.class);
        startActivity(intent);
    }

    void onClickCancel(View v){
         finish();
    }

    void addRequest(){
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, URLs.IDEAS_ADD_FILES, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                // parse success output
                Log.d(TAG,"resultResponse networkTimeMs"+response.networkTimeMs);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("description", body);
                params.put("tags", tags);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic "+auth);
                return headers;
            }

            @Override
            protected Map<String, DataPart[]> getByteData() {
                Map<String, DataPart[]> params = new HashMap<>();
                DataPart[] dataParts = new DataPart[2];
                dataParts[0] = new DataPart("file_avatar.jpg", getFileDataFromDrawable(getBaseContext(), R.mipmap.android), "image/jpeg");
                dataParts[1] = new DataPart("file_avatar.jpg", getFileDataFromDrawable(getBaseContext(), R.mipmap.empty_avatar), "image/jpeg");
                params.put("files", dataParts);
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000,1,DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Singleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


 }
