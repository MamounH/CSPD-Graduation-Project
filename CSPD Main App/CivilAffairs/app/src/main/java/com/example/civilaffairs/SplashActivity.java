package com.example.civilaffairs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;


public class SplashActivity extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Hidden status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hidden Action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //handler time and start home activity
        handler=new Handler();
        handler.postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE);
            String statusLogin = prefs.getString("key", "0");
            if (statusLogin.equals("1")){
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }else if (statusLogin.equals("0")){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        },3000);
    }

}
