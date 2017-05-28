package com.example.thien.photomagician.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thien.photomagician.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        TextView txtName = (TextView) findViewById(R.id.txtName);
        Typeface myNewFace = Typeface.createFromAsset(getAssets(), "fonts/JOKERMAN.TTF");
        txtName.setTypeface(myNewFace);
        Button btnBack=(Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}