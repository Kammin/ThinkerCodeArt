package com.example.kamin.thinkercodeart.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.model.Idea;
import com.example.kamin.thinkercodeart.volley.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Idea> ideas;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String URL_FEED = "http://thinker-codeart.44fs.preview.openshiftapps.com/restapi/ideas";

/*        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Idea>> call = apiService.getIdeas();

        call.enqueue(new Callback<List<Idea>>() {
            @Override
            public void onResponse(Call<List<Idea>> call, Response<List<Idea>> response) {
                int statusCode = response.code();
                Log.d("IDEAS","OK"+response.body().size());
                ideas = response.body();
                recyclerView.setAdapter(new IdeaAdapter(ideas, R.layout.item_idea, getApplicationContext()));
            }

            @Override
            public void onFailure (Call<List<Idea>> call, Throwable t) {
                Log.d("IDEAS","NOT");
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });*/

        Cache cache = Singleton.getInstance(this).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
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
                        Log.d(TAG," "+response.length());
                        //parseJsonFeed(response);
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

    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

/*            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
