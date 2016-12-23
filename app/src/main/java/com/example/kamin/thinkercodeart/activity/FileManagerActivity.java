package com.example.kamin.thinkercodeart.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.FileAdapter;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.columnWidth;


public class FileManagerActivity extends AppCompatActivity {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    Context context;
    File[] listDir;
    File dir;
    int widthHeightItem = 200;
    int itemMargin = 2;
    boolean hasParent=false;


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            listDir = dir.listFiles();
            fillAdapter();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_file_picker);
        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitleMargin(0, 0, 0, 0);


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
        Log.d(TAG, "path " + path);
        dir = new File(path);
        Log.d(TAG, "AbsolutePath " + dir.getAbsolutePath());

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                listDir = dir.listFiles();
                fillAdapter();
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            listDir = dir.listFiles();
            fillAdapter();
        }
    }

    void fillAdapter() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int countColumn = (metrics.widthPixels / widthHeightItem);
        int winthItem = (metrics.widthPixels / countColumn);
        ArrayList<File> prepListDir = new ArrayList<>();
        if(dir.getParentFile()!=null)
            hasParent = true;
            prepListDir.add(dir.getParentFile());
        for(File file:listDir){
           if (file.isDirectory())
            prepListDir.add(file);
        }
        for(File file:listDir){
            if (isImage(file.getName()))
                prepListDir.add(file);
        }
        listDir= prepListDir.toArray(new File[prepListDir.size()]);
        FileAdapter fileAdapter = new FileAdapter(this, listDir, winthItem, hasParent);
        RecyclerView fileRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, countColumn);
        fileRecyclerView.setLayoutManager(layoutManager);
        fileRecyclerView.setAdapter(fileAdapter);
    }


    void onClickBack(View v) {
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
