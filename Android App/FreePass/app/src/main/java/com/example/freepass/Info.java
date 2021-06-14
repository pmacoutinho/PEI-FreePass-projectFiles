package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageView home_imageView = findViewById(R.id.Home_imageView);
        ImageView backArrow_imageView = findViewById(R.id.BackArrow_imageView);
        TextView save_textView = findViewById(R.id.Save_textView);
        TextView saveInfo_textView = findViewById(R.id.SaveInfo_textView);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Home button
        home_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, MainMenu.class))
        );

        //Back arrow button
        backArrow_imageView.setOnClickListener(v -> {
            if (getIntent().getStringExtra("mode").matches("account"))
                startActivity(new Intent(this, AccountMode.class));
            else
                startActivity(new Intent(this, AnonymousMode.class));
        });

        if (getIntent().getStringExtra("mode").matches("account")) {
            save_textView.setVisibility(View.VISIBLE);
            saveInfo_textView.setVisibility(View.VISIBLE);
        } else {
            save_textView.setVisibility(View.GONE);
            saveInfo_textView.setVisibility(View.GONE);
        }

    }
}