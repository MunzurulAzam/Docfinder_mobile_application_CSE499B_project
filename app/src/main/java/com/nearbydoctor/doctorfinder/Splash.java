package com.nearbydoctor.doctorfinder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Locale;

import com.nearbydoctor.R;
import com.nearbydoctor.utils.LanguageSelectore;

public class Splash extends Activity {
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        LanguageSelectore.setLanguageIfAR( Splash.this );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_splash);
        sp = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


        Log.d("language", "" + Locale.getDefault().getDisplayLanguage());
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    if (sp.getBoolean("userfirsttime", false)) {
                        Intent i = new Intent(getBaseContext(), Home.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(getBaseContext(), Introduction.class);
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        };
        th.start();
    }


}
