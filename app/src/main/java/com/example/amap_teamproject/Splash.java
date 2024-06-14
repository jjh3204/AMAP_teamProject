package com.example.amap_teamproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.Login.RegisterActivity;

public class Splash extends AppCompatActivity {

    private static final long SPLASH_DELAY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Splash.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        }, SPLASH_DELAY);
    }
}