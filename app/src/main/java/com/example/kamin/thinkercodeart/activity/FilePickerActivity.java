package com.example.kamin.thinkercodeart.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.example.kamin.thinkercodeart.R;


public class FilePickerActivity extends AppCompatActivity {
    final static public String TAG = StartActivity.class.getSimpleName();
    Context context;

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
