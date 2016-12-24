package com.example.kamin.thinkercodeart.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class StartActivity extends AppCompatActivity {
    TextView tvLogin, tvSignup, btSigin, btSigup;
    EditText etLogin, etPass, etUsermane, etConfirmPass, etEmail;
    ImageView imageTriangleLeft, imageTriangleRight;
    String username, email, password, confirm;
    final static public String TAG = StartActivity.class.getSimpleName();
    SharedPreferences sPref;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        context = this;
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        String ActiveUser = sPref.getString(getResources().getString(R.string.ACTIVE_USER), "");

        if (!ActiveUser.equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

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
        etPass.setVisibility(View.VISIBLE);
        etUsermane.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);
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
        etUsermane.setText("");
        etEmail.setText("");
        etPass.setText("");
        etConfirmPass.setText("");

        imageTriangleRight.setVisibility(View.VISIBLE);
        imageTriangleLeft.setVisibility(View.INVISIBLE);
        tvLogin.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSignup.setTextColor(getResources().getColor(R.color.colorOrange));
        btSigin.setVisibility(View.GONE);
        btSigup.setVisibility(View.VISIBLE);
    }

    void onClickSignIn(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        if (!checkConnection())
            return;
        username = etLogin.getText().toString();
        password = etPass.getText().toString();
        loginUser();
        //String[] params = new String[]{etLogin.getText().toString(), etPass.getText().toString()};
        //new PostClass(this).execute(params);

    }

    void onClickSignUp(View v) {
        username = etUsermane.getText().toString();
        email = etEmail.getText().toString();
        password = etPass.getText().toString();
        confirm = etConfirmPass.getText().toString();
        if (checkInputData())
            registerNewUser();
    }

    void regSuccessfully() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        onClickLogin(null);
        etLogin.setText(username);
        etPass.setText(password);
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra("MESSAGE", getResources().getString(R.string.RegSuccessRegister));
        startActivity(intent);
    }


    void login() {
        String auth = username+":"+password;
        byte[] encoding = Base64.encode(auth.getBytes(),Base64.DEFAULT);
        try {
            auth = new String(encoding, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(getResources().getString(R.string.ACTIVE_USER), etLogin.getText().toString());
        ed.putString(getResources().getString(R.string.AUTH), auth);
        ed.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void error() {
        Log.d(TAG, "NoLogin... ");
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra("MESSAGE", getResources().getString(R.string.NoLogin));
        startActivity(intent);
    }

    Boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            intent.putExtra("MESSAGE", getResources().getString(R.string.NoConnection));
            startActivity(intent);
        }
        return isConnected;
    }



    void registerNewUser() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        Log.d(TAG, requestBody);
        StringRequest jsonReq = new StringRequest(Request.Method.POST,
                URLs.USERS, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode != 201){
                    Log.d(TAG, "networkResponse.statusCode " + networkResponse.statusCode);
                            Intent intent = new Intent(context, AlertDialogActivity.class);
                            intent.putExtra("MESSAGE", getResources().getString(R.string.RegFailedRegister));
                            startActivity(intent);
                }
                VolleyLog.d(TAG, "Error: " + "  " + error.toString());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "NetworkResponse " + response.statusCode);
                if (response.statusCode == 201) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onClickLogin(null);
                            etLogin.setText(username);
                            etPass.setText(password);
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
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("dataType", "TEXT");
                headers.put("async", "true");
                return headers;
            }

        };
        // Adding request to volley request queue
        Singleton.getInstance(this).addToRequestQueue(jsonReq);
    }







    void loginUser() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        Log.d(TAG, requestBody);
        StringRequest jsonReq = new StringRequest(Request.Method.POST,
                URLs.LOGIN, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "NetworkResponse " + response.toString());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode != 200){
                    Log.d(TAG, "networkResponse.statusCode " + networkResponse.statusCode);
                    error();
                }
                VolleyLog.d(TAG, "Error: " + "  " + error.toString());
            }
        }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "NetworkResponse " + response.statusCode);
                if (response.statusCode == 200) {
                    login();
                }
                return super.parseNetworkResponse(response);
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("dataType", "TEXT");
                headers.put("async", "true");
                return headers;
            }
        };
        // Adding request to volley request queue
        jsonReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        Singleton.getInstance(this).addToRequestQueue(jsonReq);
    }


    Boolean checkInputData() {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        if ((username.equals("")) | (email.equals("")) | (password.equals("")) | (confirm.equals(""))) {
            intent.putExtra("MESSAGE", getResources().getString(R.string.RegEmptyFields));
            startActivity(intent);
            return false;
        }
        if (!(email.contains("@")) | !(email.contains("."))) {
            intent.putExtra("MESSAGE", getResources().getString(R.string.RegIncorrectEmail));
            startActivity(intent);
            return false;
        }
        if (!(password.equals(confirm))) {
            intent.putExtra("MESSAGE", getResources().getString(R.string.RegIncorrectConfirmation));
            startActivity(intent);
            return false;
        }

        return true;
    }


    private class PostClass extends AsyncTask<String[], Void, Void> {
        private final Context context;
        ProgressDialog progress;
        String result = "";

        public PostClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String[]... params1) {
            try {
                String[] passed = params1[0];
                URL obj = new URL(URLs.LOGIN);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                conn.setInstanceFollowRedirects(false);

                String urlParameters = "username=" + passed[0] + "&password=" + passed[1];
                conn.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(urlParameters);
                outputStream.flush();
                outputStream.close();

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = conn.getResponseCode();
                Log.d(TAG, "Response Code ... " + conn.getResponseCode());

                if (status == 302) {
                    String newUrl = conn.getHeaderField("Location");
                    if (newUrl.contains("entered/home")) {
                        result = "LOGIN";

                    }
                    if (newUrl.contains("main?error=true")) {
                        result = "ERROR";
                    }
                    Log.d(TAG, "newUrl ... " + newUrl);
                    String Cookie = conn.getHeaderField("Set-Cookie");
                    Log.d(TAG, "Cookie ... " + Cookie);
                }
                ;

            } catch (Exception e) {
                e.printStackTrace();
            }

            StartActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result.equals("LOGIN")) {
                        login();
                        Log.d(TAG, "------------------------------" + result);
                    }
                    if (result.equals("ERROR")) {
                        error();
                        Log.d(TAG, "------------------------------" + result);
                    }
                }
            });

            return null;
        }
    }
}
