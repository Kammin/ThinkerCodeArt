package com.example.kamin.thinkercodeart.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.SelectFileAdapter;
import com.example.kamin.thinkercodeart.util.HolderData;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.MultipartRequest;
import com.example.kamin.thinkercodeart.volley.Singleton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class IdeaActivity extends AppCompatActivity {
    final static public String TAG = StartActivity.class.getSimpleName();
    String name, body, tags, auth;
    Context context;
    EditText etName, etBody, etTags;
    RecyclerView selectRecyclerView;
    int heightSelectedPhoto;
    TextView btAddFile;
    ImageButton backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        context = this;
        heightSelectedPhoto = (int) getResources().getDimension(R.dimen.SelectFileHeight);
        selectRecyclerView = (RecyclerView) findViewById(R.id.pic_recycler_view);
        btAddFile = (TextView) findViewById(R.id.btAddFile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        backButton = (ImageButton) findViewById(R.id.backButton);
        toolbar.setTitleMargin(0, 0, 0, 0);

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        auth = sPref.getString(getResources().getString(R.string.AUTH), "");
        etName = (EditText) findViewById(R.id.etName);
        etBody = (EditText) findViewById(R.id.etBody);
        etTags = (EditText) findViewById(R.id.etTags);
        etName.setText("Имя Имя");
        etBody.setText("тело тело тело");
        etTags.setText("тег тег");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    protected void onResume() {

        if(HolderData.selectedPfoto!=null)
            if(HolderData.selectedPfoto.size()>0)
            btAddFile.setText("Edit file");
        else
            btAddFile.setText("Add file");
        fillSelectedFilesAdapter();
        super.onResume();
    }

    void onClickCreate(View v) throws UnsupportedEncodingException {
        name = new String(etName.getText().toString().getBytes(), "ISO-8859-1");
        body = new String(etBody.getText().toString().getBytes(), "ISO-8859-1");
        tags = new String(etTags.getText().toString().getBytes(), "ISO-8859-1");
        addRequest();
    }

    void onClickAddFile(View v) {
        Intent intent = new Intent(this, FileManagerActivity.class);
        startActivity(intent);
    }

    void onClickCancel(View v) {
        finish();
    }

    void addRequest() {
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, URLs.IDEAS_ADD_FILES, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.d(TAG, "resultResponse networkTimeMs " + response.networkTimeMs + " statusCode " + response.statusCode);
                if (response.statusCode == 200) {
                    Intent intent = new Intent(context, AlertDialogActivity.class);
                    intent.putExtra("MESSAGE", getResources().getString(R.string.IdeaAdded));
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(context, AlertDialogActivity.class);
                intent.putExtra("MESSAGE", getResources().getString(R.string.ErrorIdeaAdd)+" StatusCode "+error.networkResponse.statusCode+" "+error.networkResponse.toString());
                startActivity(intent);
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
                headers.put("Authorization", "Basic " + auth);
                return headers;
            }

            @Override
            protected Map<String, DataPart[]> getByteData() throws IOException {
                Map<String, DataPart[]> params = new HashMap<>();
                if (HolderData.selectedPfoto != null) {
                    DataPart[] dataParts = new DataPart[HolderData.selectedPfoto.size()];
                    for (int i = 0; i < dataParts.length; i++) {
                        String name = HolderData.selectedPfoto.get(i).getName();
                        dataParts[i] = new DataPart(name, FileUtils.readFileToByteArray(HolderData.selectedPfoto.get(i)), getMimeType(name));
                        Log.d(TAG, "" + name + "" + getMimeType(name));
                    }
                    params.put("files", dataParts);
                }
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Singleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    public String getMimeType(String path) {
        String type;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = path.substring(path.lastIndexOf(".") + 1);
        type = mime.getMimeTypeFromExtension(extension);
        if (type != null) {
            return type;
        }
        return "";
    }

    void fillSelectedFilesAdapter() {
        if (HolderData.selectedPfoto != null) {
            File[] selectedPhoto = HolderData.selectedPfoto.toArray(new File[HolderData.selectedPfoto.size()]);
            SelectFileAdapter selectFileAdapter = new SelectFileAdapter(context, selectedPhoto, heightSelectedPhoto);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            selectRecyclerView.setLayoutManager(layoutManager);
            selectRecyclerView.setAdapter(selectFileAdapter);

        }
    }

    void onClickBack(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
