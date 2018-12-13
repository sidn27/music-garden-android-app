package com.musicgarden.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.musicgarden.android.lib.Background;


/**
 * Created by Necra on 11-03-2018.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MusicGarden", MODE_PRIVATE);

        boolean firstRun = pref.getBoolean("FirstRun", true);

        int SPLASH_TIME_OUT = 500;

        /* check if it is the first run of our application */

        if (!firstRun) {

            /* go to main activity */

            Runnable mainActivityRunnable = new Runnable() {

                @Override
                public void run() {

                    Intent intent;

                    intent = new Intent(SplashActivity.this, MainActivity.class);

                    Background.getInstance().unregisterRunnable(this);

                    startActivity(intent);
                    finish();

                }

            };

            Background.getInstance().registerRunnable(mainActivityRunnable, SPLASH_TIME_OUT);


        } else {

            /* go to the welcome activity */

            Runnable welcomeActivityRunnable = new Runnable() {

                @Override
                public void run() {

                    Background.getInstance().unregisterRunnable(this);

                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);

                    startActivity(intent);
                    finish();

                }

            };

            Background.getInstance().registerRunnable(welcomeActivityRunnable, SPLASH_TIME_OUT);


        }

    }
}