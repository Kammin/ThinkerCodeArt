package com.example.kamin.thinkercodeart.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.util.URL;
import com.example.kamin.thinkercodeart.volley.Singleton;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends AppCompatActivity {
    TextView tvLogin, tvSignup, btSigin, btSigup;
    EditText etLogin, etPass, etUsermane, etConfirmPass, etEmail;
    ImageView imageTriangleLeft, imageTriangleRight;
    final static public String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        TextView textView = (TextView) findViewById(R.id.textViewLink);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvSignup = (TextView) findViewById(R.id.tvSignup);
        btSigin = (TextView) findViewById(R.id.btSigin);
        btSigup = (TextView) findViewById(R.id.btSigup);
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPass = (EditText) findViewById(R.id.etPass);
        etUsermane = (EditText) findViewById(R.id.etUsermane);
        etConfirmPass = (EditText) findViewById(R.id.etConfirmPass);
        etEmail = (EditText) findViewById(R.id.etEmail);
        imageTriangleLeft = (ImageView) findViewById(R.id.imageTriangleLeft);
        imageTriangleRight = (ImageView) findViewById(R.id.imageTriangleRight);

    }

    void onClickLogin(View v) {
        etLogin.setVisibility(View.VISIBLE);
        etUsermane.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);
        etPass.setVisibility(View.VISIBLE);
        etConfirmPass.setVisibility(View.GONE);

        imageTriangleRight.setVisibility(View.INVISIBLE);
        imageTriangleLeft.setVisibility(View.VISIBLE);
        tvLogin.setTextColor(getResources().getColor(R.color.colorOrange));
        tvSignup.setTextColor(getResources().getColor(R.color.colorWhite));
        btSigup.setVisibility(View.GONE);
        btSigin.setVisibility(View.VISIBLE);
    }

    void onClickSignup(View v) {
        etLogin.setVisibility(View.GONE);
        etUsermane.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);
        etPass.setVisibility(View.VISIBLE);
        etConfirmPass.setVisibility(View.VISIBLE);


        imageTriangleRight.setVisibility(View.VISIBLE);
        imageTriangleLeft.setVisibility(View.INVISIBLE);
        tvLogin.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSignup.setTextColor(getResources().getColor(R.color.colorOrange));
        btSigin.setVisibility(View.GONE);
        btSigup.setVisibility(View.VISIBLE);
    }

    void onClickSignIn(View v) {
        Log.d(TAG, "onClickSignIn");
        login();
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

    }

    void login() {
        Log.d(TAG, "Login");

        final StringRequest loginReq = new StringRequest(Request.Method.POST,
                URL.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Response: " + response);
                Log.d(TAG, "response length = " + response.length());
                Log.d(TAG, "URL.LOGIN = " + URL.LOGIN);

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getNetworkTimeMs() + "  " + error.toString());
            }
        }) {
            @Override
            protected void deliverResponse(String response) {
                Log.d(TAG, "Response: " + response);
                super.deliverResponse(response);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Response Code " + response.statusCode);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", "admin");
                params.put("password", "admin");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        Singleton.getInstance(this).addToRequestQueue(loginReq);

    }

}
