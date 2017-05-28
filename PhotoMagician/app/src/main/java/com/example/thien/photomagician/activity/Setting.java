package com.example.thien.photomagician.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thien.photomagician.R;

import java.util.Locale;

public class Setting extends AppCompatActivity {
    RadioGroup radGroup;
    RadioButton radEng;
    RadioButton radVie;
    TextView txtLanguage;
    private Locale myLocale;
    String lang;
    Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        lang=getIntent().getStringExtra("key");
        //Toast.makeText(getApplicationContext(), lang, Toast.LENGTH_SHORT).show();
        btnBack = (Button) findViewById(R.id.btnBack);
        radGroup = (RadioGroup) findViewById(R.id.radGr);
        radEng = (RadioButton) findViewById(R.id.radEng);
        radVie = (RadioButton) findViewById(R.id.radVie);
        txtLanguage = (TextView) findViewById(R.id.txtLanguage);
//        if(lang =="vi") {
//            Toast.makeText(getApplicationContext(), lang, Toast.LENGTH_SHORT).show();
//            radEng.setChecked(true);
//        }else if(lang =="en"){
//            radVie.setChecked(true);
//            Toast.makeText(getApplicationContext(), lang, Toast.LENGTH_SHORT).show();
//        }
        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("key", lang);
                startActivity(intent);
            }
        });
        radGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.radEng) {
                    //Toast.makeText(getApplicationContext(), "Eng", Toast.LENGTH_SHORT).show();
                    lang="en";
                }
                else if(i==R.id.radVie){
                    //Toast.makeText(getApplicationContext(), "Vie", Toast.LENGTH_SHORT).show();
                    lang="vi";
                }
                changeLang(lang);
            }
        });
        loadLocale();
    }
    public void loadLocale()
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }
    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }
    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        updateTexts();
    }
    private void updateTexts()
    {
        txtLanguage.setText(R.string.txtLanguage);
        radEng.setText(R.string.radEng);
        radVie.setText(R.string.radVie);
        btnBack.setText(R.string.btnBack);
    }
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
