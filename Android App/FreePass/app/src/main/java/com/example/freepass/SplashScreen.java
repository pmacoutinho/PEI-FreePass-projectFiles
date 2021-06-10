package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Splash Screen will appear for 1.1 seconds before it goes to the Main Menu
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, MainMenu.class));
            finish();
        }, 1100);
    }
}
