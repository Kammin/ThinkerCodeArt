package com.example.kamin.thinkercodeart.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.example.kamin.thinkercodeart.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView textView = (TextView) findViewById(R.id.textViewLink);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


/*        Drawable login_activity_top_background = getResources().getDrawable(R.drawable.rounded_corner);
        login_activity_top_background.setAlpha(50);
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setBackgroundDrawable(login_activity_top_background);
        TextView tvRegistration = (TextView) findViewById(R.id.tvRegistration);
        tvRegistration.setBackgroundDrawable(login_activity_top_background);*/
    }
}
