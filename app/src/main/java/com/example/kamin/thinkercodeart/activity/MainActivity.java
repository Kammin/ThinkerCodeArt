package com.example.kamin.thinkercodeart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.IdeaAdapter;
import com.example.kamin.thinkercodeart.model.Idea;
import com.example.kamin.thinkercodeart.util.HolderData;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.kamin.thinkercodeart.util.URLs.IDEAS_PATH;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    FloatingActionButton fab;
    Toolbar toolbar;
    SharedPreferences sPref;
    String userName;
    RecyclerView recyclerView;
    MainActivity mainActivity;
    public static final String TAG = MainActivity.class.getSimpleName();
    public boolean canLoad = false;
    String auth,curentUserID;
    LinearLayoutManager linearLayoutManager;
    private String loading;
    private int loadForItem = 10;
    String endIdeaID = "";
    IdeaAdapter ideaAdapter;
    String criteria, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;


        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sPref.getString(getResources().getString(R.string.ACTIVE_USER), "");
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitle(null);
        toolbar.setTitleMargin(0, 0, 0, 0);
        HolderData.selectedPfoto = new ArrayList<>();

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(this);
        auth = sPref.getString(getResources().getString(R.string.AUTH), "");
        curentUserID = sPref.getString(getResources().getString(R.string.ACTIVE_USER_ID), "");

        fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HolderData.selectedPfoto = null;
                Intent intent = new Intent(getApplicationContext(), IdeaActivity.class);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                startActivity(intent);
                Log.d(TAG, "fab Click ");
            }
        });
        final ImageView imageView = (ImageView) toolbar.findViewById(R.id.logo);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.setLeft(15);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        Singleton.getInstance(getApplicationContext()).ideas = new ArrayList<>();
        ideaAdapter = new IdeaAdapter(Singleton.getInstance(getApplicationContext()).ideas, R.layout.item_idea, getApplicationContext(), mainActivity);
        recyclerView.setAdapter(ideaAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Scroll Down
                    if (fab.isShown()) {
                        fab.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });

        if (Singleton.getInstance(this).ideas.size() == 0)
            feedIdeas(IDEAS_PATH, false);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if ((totalItemCount < firstVisibleItem + loadForItem)&&(canLoad)) {
                    Log.d(TAG, "loading ---- canLoad " + canLoad);
                    if (loading.equals(getString(R.string.LoadIdea))) {
                        feedIdeas(URLs.IDEAS + "/part/" + endIdeaID, true);
                        Log.d(TAG, "loading ----  " + URLs.IDEAS + "/part/" + endIdeaID);
                    }
                    if (loading.equals(getString(R.string.LoadSerch))) {
                        search(criteria, content,endIdeaID);
                        Log.d(TAG, "loading ---- search " +" content "+content+" endIdeaID"+endIdeaID);
                    }
                }
            }
        });
    }

    void feedIdeas(String URL, boolean add) {
        canLoad = false;
        final boolean thisAdd = add;
        Log.d(TAG, "LOAD feed URL" + URL);
        loading = getResources().getString(R.string.LoadIdea);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (!thisAdd) {
            progressBar.setVisibility(View.VISIBLE);
            Singleton.getInstance(getApplicationContext()).ideas.clear();
            ideaAdapter.notifyDataSetChanged();
            recyclerView.invalidate();
        }
        JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.GET,
                URL, null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    Log.d(TAG, "response length = " + response.length());
                    parseJsonFeed(response);
                    if (thisAdd)
                        ideaAdapter.notifyDataSetChanged();
                    else
                        recyclerView.setAdapter(ideaAdapter);
                    Log.d(TAG, "ideas length = " + Singleton.getInstance(getApplicationContext()).ideas.size());
                    progressBar.setVisibility(View.GONE);
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getNetworkTimeMs() + "  " + error.toString());
                progressBar.setVisibility(View.GONE);
                canLoad = false;
            }
        });
        jsonReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Singleton.getInstance(this).addToRequestQueue(jsonReq);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.getItem(1).setTitle(userName);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     */
    private void parseJsonFeed(JSONArray response) {
        Log.d(TAG, "LOAD array lenght " + response.length());
        for (int i = 0; i < response.length(); i++) {

            try {
                JSONObject obj = response.getJSONObject(i);
                Idea idea = new Idea();
                idea.setIdeaId(obj.getString("ideaId"));
                if (i == response.length() - 1) {
                    endIdeaID = idea.getIdeaId();
                    canLoad = true;
                    Log.d(TAG, "endIdeaID " + endIdeaID);
                }
                idea.setName(obj.getString("name"));
                idea.setBodyIdea(obj.getString("description"));
                idea.setUserId(obj.getString("userId"));
                idea.setUsername(obj.getString("username"));
                List<String> files = new ArrayList<>();
                try {
                    JSONArray objFiles = obj.getJSONArray("files");
                    for (int j = 0; j < objFiles.length(); j++) {
                        String file = objFiles.get(j).toString();
                        files.add(file);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException " + i + " " + e.getMessage());
                    e.printStackTrace();
                }
                idea.setFiles(files);

                JSONArray objTags = obj.getJSONArray("tags");
                List<String> tags = new ArrayList<>();
                for (int k = 0; k < objTags.length(); k++) {
                    String tag = objTags.get(k).toString();
                    tags.add(tag);
                }
                idea.setTags(tags);

                idea.setLikes(obj.getInt("likes"));
                idea.setDislikes(obj.getInt("dislikes"));
                idea.setUserLikeStatus(obj.getInt("userLikeStatus"));

                Singleton.getInstance(this).ideas.add(idea);
                Log.d(TAG, "ideas.add " + Singleton.getInstance(this).ideas.size());
            } catch (JSONException e) {
                Log.d(TAG, "JSONException " + i + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void logOut() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(getResources().getString(R.string.ACTIVE_USER), "");
        ed.putString(getResources().getString(R.string.AUTH), "");
        ed.commit();
        Singleton.getInstance(this).ideas = null;
        String ActiveUser = sPref.getString(getResources().getString(R.string.ACTIVE_USER), "");
        Log.d(TAG, "logOut");
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ideas:
                feedIdeas(IDEAS_PATH, false);
                return true;

            case R.id.action_my_ideas:
                Singleton.getInstance(this).ideas.clear();
                ideaAdapter.notifyDataSetChanged();
                search("author",curentUserID,"");
                return true;

            case R.id.action_user:

                return true;

            case R.id.action_logout:
                logOut();
                return true;

            case R.id.refresh:
                Singleton.getInstance(getApplicationContext()).ideas.clear();
                ideaAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
                feedIdeas(IDEAS_PATH, false);
                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        if(HolderData.needReload){
            HolderData.needReload=false;
            feedIdeas(IDEAS_PATH, false);
        }
        super.onResume();
    }

    public void search(String criteria, String content, String part) {
        this.criteria = criteria;
        this.content = content;
        canLoad = false;
        Log.d(TAG, "LOAD search  criteria" + criteria+" content "+content+" part "+part);
        loading = getResources().getString(R.string.LoadSerch);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("criteria", criteria);
            jsonBody.put("content", content);
            jsonBody.put("part", part);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();
        Log.d(TAG, requestBody);
        recyclerView.invalidate();
        JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.POST,
                URLs.SERCH, null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "LOAD Response: " + response.toString());
                if (response != null) {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "response length = " + response.length());
                    parseJsonFeed(response);
                    progressBar.setVisibility(View.GONE);
                    ideaAdapter.notifyDataSetChanged();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                canLoad = false;
                Log.d(TAG, "LOAD Error: " + error.getNetworkTimeMs() + "  " + error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
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
                headers.put("Authorization", "Basic " + auth);
                return headers;
            }
        };

        jsonReq.setRetryPolicy(new DefaultRetryPolicy(20000, 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Singleton.getInstance(this).addToRequestQueue(jsonReq);
    }

    public void deletIdea(String ideaID, int pos) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        final int position = pos;
        StringRequest jsonReq = new StringRequest(Request.Method.DELETE,
                URLs.IDEAS + "/" + ideaID, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    progressBar.setVisibility(View.GONE);
                    Singleton.getInstance(getApplicationContext()).ideas.remove(position);
                    ideaAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(getApplicationContext(), AlertDialogActivity.class);
                    intent.putExtra("MESSAGE", getResources().getString(R.string.IdeaDeleted));
                    startActivity(intent);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error Response: " + error.getNetworkTimeMs() + "  " + error.toString());
                progressBar.setVisibility(View.GONE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + auth);
                return headers;
            }

        };

        jsonReq.setRetryPolicy(new DefaultRetryPolicy(20000, 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        Singleton.getInstance(this).addToRequestQueue(jsonReq);
    }


}
