package com.example.kamin.thinkercodeart.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.kamin.thinkercodeart.R;


public class FilePickerActivity extends AppCompatActivity {
    final static public String TAG = StartActivity.class.getSimpleName();
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_file_picker);
        context = this;


    }

}
