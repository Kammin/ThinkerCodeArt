package com.example.kamin.thinkercodeart.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.IdeaAdapter;
import com.example.kamin.thinkercodeart.model.Idea;
import com.example.kamin.thinkercodeart.rest.ApiClient;
import com.example.kamin.thinkercodeart.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    List<Idea> ideas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Idea>> call = apiService.getIdeas();
        call.enqueue(new Callback<List<Idea>>() {
            @Override
            public void onResponse(Call<List<Idea>> call, Response<List<Idea>> response) {
                int statusCode = response.code();
                Log.d("IDEAS","OK"+response.body().size());
                ideas = response.body();
/*                for(int i = 0; i<response.body().size(); i++){
                    Idea idea = response.body().get(i);
                    Author author = idea.getAuthor();
                    Date date = new Date(Long.valueOf(idea.getIdeaId().substring(0, 8 ), 16) * 1000);
                    Log.d("IDEAS","  ");
                    Log.d("IDEAS","Id "+idea.getIdeaId());
                    Log.d("IDEAS","Name "+idea.getName());
                    Log.d("IDEAS","Date "+date);
                    Log.d("IDEAS","Desc "+idea.getBodyIdea());
                    if(author!=null) {
                        Log.d("IDEAS", "      AuthorId " + author.getUserId());
                        Log.d("IDEAS", "      AuthorUsername " + author.getUsername());
                        Log.d("IDEAS", "      AuthorEmail " + author.getEmail());
                        Log.d("IDEAS", "      AuthorEnabled " + author.getEnabled());
                        Log.d("IDEAS", "      AuthorId " + author.getRoles().toString());
                    }
                    Log.d("IDEAS","Files "+idea.getFiles().toString());
                    Log.d("IDEAS","Tags "+idea.getTags().toString());
                }*/
                recyclerView.setAdapter(new IdeaAdapter(ideas, R.layout.item_idea, getApplicationContext()));
            }

            @Override
            public void onFailure (Call<List<Idea>> call, Throwable t) {
                Log.d("IDEAS","NOT");
                // Log error here since request failed
                Log.e("", t.toString());
            }
        });






/*        Call<List<Idea>> call = apiService.getIdeas();
        call.enqueue(new Callback<List<Idea>>() {
            @Override
            public void onResponse(Call<List<Idea>> call, Response<List<Idea>> response) {
                Log.d("IDEAS","OK"+response.body().size());
                for(int i = 0; i<response.body().size(); i++){
                    Idea idea = response.body().get(i);
                    Author author = idea.getAuthor();
                    Date date = new Date(Long.valueOf(idea.getIdeaId().substring(0, 8 ), 16) * 1000);
                    Log.d("IDEAS","  ");
                    Log.d("IDEAS","Id "+idea.getIdeaId());
                    Log.d("IDEAS","Name "+idea.getName());
                    Log.d("IDEAS","Date "+date);
                    Log.d("IDEAS","Desc "+idea.getBodyIdea());
                    if(author!=null) {
                        Log.d("IDEAS", "      AuthorId " + author.getUserId());
                        Log.d("IDEAS", "      AuthorUsername " + author.getUsername());
                        Log.d("IDEAS", "      AuthorEmail " + author.getEmail());
                        Log.d("IDEAS", "      AuthorEnabled " + author.getEnabled());
                        Log.d("IDEAS", "      AuthorId " + author.getRoles().toString());
                    }
                    Log.d("IDEAS","Files "+idea.getFiles().toString());
                    Log.d("IDEAS","Tags "+idea.getTags().toString());
                }
                Log.d("IDEAS","--------------------------------");
            }

            @Override
            public void onFailure(Call<List<Idea>> call, Throwable t) {
                Log.d("IDEAS","NOT");
            }
        });*/

    }
}
