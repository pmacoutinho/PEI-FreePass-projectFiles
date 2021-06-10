package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

    //TODO: [LOW PRIORITY] ADD "i" INFORMATION ICON AT THE BOTTOM

public class MainMenu extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button anonymousMode_button = findViewById(R.id.AnonymousMode_button);
        Button accountMode_button = findViewById(R.id.AccountMode_button);

        anonymousMode_button.setOnClickListener(v ->
                startActivity(new Intent(this, AnonymousMode.class))
        );

        accountMode_button.setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() != null)
                startActivity(new Intent(this, AccountMode.class));
            else
                startActivity(new Intent(this, Login.class));

            finish();
        });

    }
}
