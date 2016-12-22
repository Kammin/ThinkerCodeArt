package com.example.kamin.thinkercodeart.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.FileAdapter;

import java.io.File;


public class FileManagerActivity extends AppCompatActivity {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    Context context;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_file_picker);
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitleMargin(0,0,0,0);



        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
        Log.d(TAG,"path "+path);
        //String path = Environment.getExternalStorageDirectory();
        File dir = new File(path);
        Log.d(TAG,"AbsolutePath "+dir.getAbsolutePath());
        Log.d(TAG,"isDirectory "+dir.isDirectory());
        Log.d(TAG,"canRead "+dir.canRead());
        Log.d(TAG,"canWrite "+dir.canWrite());
        Log.d(TAG,"canExecute "+dir.canExecute());

       // Log.d(TAG,"len "+dir.list().toString());
        File[] content = dir.listFiles();
        Log.d(TAG,"len "+content.length);
        FileAdapter fileAdapter = new FileAdapter(this,content);
        RecyclerView fileRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileRecyclerView.setAdapter(fileAdapter);
    }

    void onClickBack(View v){
        finish();
    }

    public static boolean isImage(String name) {
        String suffix = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        if (suffix.length() == 0)
            return false;
        if (suffix.equals("svg"))
            return false;
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        return mime != null && mime.contains("image");
    }
}
