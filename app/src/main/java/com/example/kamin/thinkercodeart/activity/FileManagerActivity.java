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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.FileAdapter;
import com.example.kamin.thinkercodeart.util.GridAutofitLayoutManager;

import java.io.File;

import static android.R.attr.columnWidth;


public class FileManagerActivity extends AppCompatActivity {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    Context context;
    File[] listDir;
    File dir;
    GridAutofitLayoutManager gridLayoutManager;
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

    void fillAdapter(){
        FileAdapter fileAdapter = new FileAdapter(this, listDir);
        RecyclerView fileRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
       // gridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //gridLayoutManager.setAutoMeasureEnabled(true);
        int columns = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(this,columns);

        Log.d(TAG,"columnWidth "+columnWidth);
        Log.d(TAG,"columnWidth "+context.getResources().getDisplayMetrics().widthPixels);
       // gridLayoutManager.setMeasuredDimension(100,100);
        //fileRecyclerView.setHasFixedSize(true);
       // gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
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
