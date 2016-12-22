package com.example.kamin.thinkercodeart.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.IdeaAdapter;
import com.example.kamin.thinkercodeart.model.Author;
import com.example.kamin.thinkercodeart.model.Idea;
import com.example.kamin.thinkercodeart.util.AlertDialogActivity;
import com.example.kamin.thinkercodeart.util.URLs;
import com.example.kamin.thinkercodeart.volley.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    FloatingActionButton fab;
    Toolbar toolbar;
    SharedPreferences sPref;
    String userName;
    RecyclerView recyclerView;
    final FragmentManager manager = getSupportFragmentManager();
    public static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sPref.getString(getResources().getString(R.string.ACTIVE_USER), "");
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitle(null);
        toolbar.setTitleMargin(0, 0, 0, 0);

        fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (Singleton.getInstance(this).ideas == null)
            feedIdeas();
        else
            recyclerView.setAdapter(new IdeaAdapter(Singleton.getInstance(getApplicationContext()).ideas, R.layout.item_idea, getApplicationContext()));

    }

    void feedIdeas() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Cache cache = Singleton.getInstance(this).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URLs.IDEAS);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONArray(data));
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonArrayRequest jsonReq = new JsonArrayRequest(Request.Method.GET,
                    URLs.IDEAS, null, new com.android.volley.Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        Log.d(TAG, "response length = " + response.length());
                        Singleton.getInstance(getApplicationContext()).ideas = new ArrayList<>();
                        parseJsonFeed(response);
                        Log.d(TAG, "ideas length = " + Singleton.getInstance(getApplicationContext()).ideas.size());
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(new IdeaAdapter(Singleton.getInstance(getApplicationContext()).ideas, R.layout.item_idea, getApplicationContext()));
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getNetworkTimeMs() + "  " + error.toString());
                    progressBar.setVisibility(View.GONE);
                }
            });
            // Adding request to volley request queue
            Singleton.getInstance(this).addToRequestQueue(jsonReq);
        }

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

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Idea idea = new Idea();
                idea.setIdeaId(obj.getString("ideaId"));
                idea.setName(obj.getString("name"));
                idea.setBodyIdea(obj.getString("description"));

                Author author = new Author();
                JSONObject objAuthor = obj.getJSONObject("author");
                author.setUserId(objAuthor.getString("userId"));
                author.setEmail(objAuthor.getString("email"));
                author.setUsername(objAuthor.getString("username"));
                idea.setAuthor(author);

                JSONArray objFiles = obj.getJSONArray("files");
                List<String> files = new ArrayList<>();
                for (int j = 0; j < objFiles.length(); j++) {
                    String file = objFiles.get(j).toString();
                    files.add(file);
                }
                idea.setFiles(files);

                JSONArray objTags = obj.getJSONArray("tags");
                List<String> tags = new ArrayList<>();
                for (int k = 0; k < objTags.length(); k++) {
                    String tag = objTags.get(k).toString();
                    tags.add(tag);
                }
                idea.setTags(tags);

                Singleton.getInstance(this).ideas.add(idea);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    void logOut() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(getResources().getString(R.string.ACTIVE_USER), "");
        ed.commit();
        String ActiveUser = sPref.getString(getResources().getString(R.string.ACTIVE_USER), "");
        Log.d(TAG, "logOut |" + ActiveUser + "|");
        Log.d(TAG, "logOut");
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_User:

                return true;

            case R.id.action_logout:
                logOut();
                return true;

            case R.id.refresh:
                Intent intent = new Intent(this, AlertDialogActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
