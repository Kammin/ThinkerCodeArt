package com.example.kamin.thinkercodeart.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.kamin.thinkercodeart.volley.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Idea> ideas;
    ProgressBar progressBar;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(myToolbar);


        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String URL_FEED = "http://thinker-codeart.44fs.preview.openshiftapps.com/restapi/ideas";
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Cache cache = Singleton.getInstance(this).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
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
                    URL_FEED, null, new com.android.volley.Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        Log.d(TAG, "response length = " + response.length());
                        ideas = new ArrayList<>();
                        parseJsonFeed(response);
                        Log.d(TAG, "ideas length = " + ideas.size());
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAdapter(new IdeaAdapter(ideas, R.layout.item_idea, getApplicationContext()));
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            Singleton.getInstance(this).addToRequestQueue(jsonReq);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
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

                ideas.add(idea);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.refresh:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
