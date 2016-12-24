package com.example.kamin.thinkercodeart.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.kamin.thinkercodeart.R;

public class AlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        String mess = getIntent().getStringExtra("MESSAGE");
        TextView tvMessage = (TextView) findViewById(R.id.Message);
        tvMessage.setText(mess);
    }

    void close(View V){
      finish();
    }
}
