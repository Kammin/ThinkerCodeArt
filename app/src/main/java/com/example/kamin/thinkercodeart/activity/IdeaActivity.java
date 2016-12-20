package com.example.kamin.thinkercodeart.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.util.AlertDialogActivity;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.Singleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class IdeaActivity extends AppCompatActivity {
    final static public String TAG = StartActivity.class.getSimpleName();
    String name, body, tags;
    Context context;
    byte[] fileData1;
    byte[] fileData2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        context = this;
        byte[] fileData1 = getFileDataFromDrawable(context, R.drawable.ic_refresh);
        byte[] fileData2 = getFileDataFromDrawable(context, R.drawable.fab_background);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.CreateIdea);
        toolbar.setTitleMargin(0,0,0,0);
        ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    void onClickCreate(View v){
        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etBody = (EditText) findViewById(R.id.etBody);
        EditText etTags = (EditText) findViewById(R.id.etTags);
        name = etName.getText().toString();
        body = etBody.getText().toString();
        tags = etTags.getText().toString();

    }

    void onClickCancel(View v){
         finish();
    }

    void addRequest(){
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URLs.IDEAS_ADD_FILES, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + "  " + error.toString());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "NetworkResponse " + response.statusCode);
                if (response.statusCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, AlertDialogActivity.class);
                            intent.putExtra("MESSAGE", getResources().getString(R.string.RegSuccessRegister));
                            startActivity(intent);
                        }
                    });
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.add("mobile_number","mobileNumber");
                String postBody = createPostBody(params);

                return postBody.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","Kamin","kam");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }

        };

        // Adding request to volley request queue
        Singleton.getInstance(this).addToRequestQueue(strReq);
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
        // the first file
        buildPart(dos, fileData1, "ic_action_android.png");
        // the second file
        buildPart(dos, fileData2, "ic_action_book.png");
        // send multipart form data necesssary after file data
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        // pass to multipart body
        multipartBody = bos.toByteArray();
    } catch (IOException e) {
        e.printStackTrace();
    }

    private void buildPart(DataOutputStream dataOutputStream, byte[] fileData, String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        // read file and write it into form...
        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataOutputStream.writeBytes(lineEnd);
    }

    private byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
