package com.example.todo_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    public static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        handler.postDelayed(r, 100);
    }

    @Override
    protected void onStart() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onStart();

        if(flag){
            SplashActivity.this.finish();
            System.exit(0);
            super.onStart();
        }else {
            super.onStart();
        }
    }
}