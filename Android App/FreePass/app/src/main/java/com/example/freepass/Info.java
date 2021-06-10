package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Objects;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageView home_imageView = findViewById(R.id.Home_imageView);
        ImageView backArrow_imageView = findViewById(R.id.BackArrow_imageView);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Home button
        home_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, MainMenu.class))
        );

        //Back arrow button
        backArrow_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, AnonymousMode.class))
        );
    }
}