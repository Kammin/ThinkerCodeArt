package com.example.kamin.thinkercodeart.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kamin.thinkercodeart.R;
import com.example.kamin.thinkercodeart.adapter.FileAdapter;
import com.example.kamin.thinkercodeart.adapter.SelectFileAdapter;
import com.example.kamin.thinkercodeart.util.HolderData;
import com.example.kamin.thinkercodeart.util.RecyclerItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.example.kamin.thinkercodeart.R.string.PATH;


public class FileManagerActivity extends AppCompatActivity {
    final static public String TAG = FileManagerActivity.class.getSimpleName();
    Context context;
    File[] listDir;
    File dir;
    int widthHeightItem = 200;
    int heightSelectedPhoto;
    boolean hasParent = false;
    RecyclerView fileRecyclerView, selectRecyclerView;
    FileManagerActivity fileManagerActivity = this;
    String path;
    long length=0;
    SharedPreferences sPref;
    ImageButton backButton;
    TextView selectPhoto;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

            fillAdapter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
            if (HolderData.selectedPfoto != null) {
                fillSelectedFilesAdapter();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_file_picker);
        context = this;
        fileRecyclerView = (RecyclerView) findViewById(R.id.file_recycler_view);
        selectRecyclerView = (RecyclerView) findViewById(R.id.pic_recycler_view);
        selectPhoto = (TextView) findViewById(R.id.selectPhoto);
        backButton = (ImageButton) findViewById(R.id.backButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitleMargin(0, 0, 0, 0);

        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        path = sPref.getString(getResources().getString(PATH), "");
        if (!path.equals(""))
            Log.d(TAG, "PATH commit " + path);
        if (path == "") {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
            if (!new File(path).exists())
                path = "/";
        }

        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        heightSelectedPhoto = (int) resources.getDimension(R.dimen.SelectFileHeight);
        Log.d(TAG, "heightSelectedPhoto " + heightSelectedPhoto);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                fillAdapter(path);
                if (HolderData.selectedPfoto != null) {
                    fillSelectedFilesAdapter();
                }
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            fillAdapter(path);
            fillSelectedFilesAdapter();
        }

    }

    public void fillAdapter(String path) {
        Log.d(TAG, "path " + path);
        dir = new File(path);
        listDir = dir.listFiles();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int countColumn = (metrics.widthPixels / widthHeightItem);
        int winthItem = (metrics.widthPixels / countColumn);

        ArrayList<File> prepListDir = new ArrayList<>();
        if (dir.getParent() != null) {
            Log.d(TAG, "dir.getParent() " + dir.getParent());
            hasParent = true;
            prepListDir.add(dir.getParentFile());
        } else {
            hasParent = false;
        }
        if (listDir != null) {
            for (File file : listDir) {
                if (file.isDirectory() && (file.canRead() && (file.canExecute()))) {
                    prepListDir.add(file);
                }
            }
            for (File file : listDir) {
                if (isImage(file.getName())) {
                    prepListDir.add(file);
                }

            }
        }

        listDir = prepListDir.toArray(new File[prepListDir.size()]);
        Log.d(TAG, "listDir " + listDir.length);
        FileAdapter fileAdapter = new FileAdapter(this, listDir, winthItem, hasParent);
        GridLayoutManager layoutManager = new GridLayoutManager(this, countColumn);
        fileRecyclerView.setLayoutManager(layoutManager);
        fileRecyclerView.setAdapter(fileAdapter);

        fileRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fileRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (listDir[position].isDirectory()) {
                            sPref = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString(getResources().getString(PATH), listDir[position].getAbsolutePath());
                            ed.commit();
                            fileManagerActivity.fillAdapter(listDir[position].getAbsolutePath());
                        } else
                        {
                            if (HolderData.selectedPfoto == null)
                                HolderData.selectedPfoto = new ArrayList<>();
                            if(HolderData.selectedPfoto.size()<HolderData.MaxPhotoCount){
                                if ((length + listDir[position].length()) < HolderData.MaxWeithPhotoArray) {
                                    Collections.reverse(HolderData.selectedPfoto);
                                    HolderData.selectedPfoto.add(listDir[position]);
                                    Collections.reverse(HolderData.selectedPfoto);
                                    fillSelectedFilesAdapter();
                                } else {
                                    Intent intent = new Intent(context, AlertDialogActivity.class);
                                    intent.putExtra("MESSAGE", getResources().getString(R.string.ExceedingWeight));
                                    startActivity(intent);
                                }
                            }else
                            {
                                Intent intent = new Intent(context, AlertDialogActivity.class);
                                intent.putExtra("MESSAGE", getResources().getString(R.string.ExceedingCount));
                                startActivity(intent);
                            }

                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {


                    }
                })
        );
        selectRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, selectRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        HolderData.selectedPfoto.remove(position);
                        fillSelectedFilesAdapter();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );
    }

    void fillSelectedFilesAdapter() {
        if (HolderData.selectedPfoto != null) {
            File[] selectedPhoto = HolderData.selectedPfoto.toArray(new File[HolderData.selectedPfoto.size()]);
            SelectFileAdapter selectFileAdapter = new SelectFileAdapter(context, selectedPhoto, heightSelectedPhoto);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            selectRecyclerView.setLayoutManager(layoutManager);
            selectRecyclerView.setAdapter(selectFileAdapter);
            if(selectedPhoto.length>0) {
                length=0;
                for(File file:selectedPhoto){
                    length += file.length();
                }
                backButton.setImageResource(R.drawable.ic_check_black_24dp);
                selectPhoto.setText("("+(length/(1024))+"/"+"Kb)   "+selectedPhoto.length + " " + getResources().getString(R.string.selectPhoto));
            }
            else{
                backButton.setImageResource(R.drawable.ic_arrow_back_24dp);
                selectPhoto.setText(getResources().getString(R.string.selectPhoto));
            }
        }
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
