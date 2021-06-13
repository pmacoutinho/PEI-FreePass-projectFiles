package com.example.freepass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView backArrow_imageView = findViewById(R.id.BackArrow_imageView);
        ImageView home_imageView = findViewById(R.id.Home_imageView);
        TextView email_textView = findViewById((R.id.Email_textView));
        EditText email_editText = findViewById(R.id.Email_editText);
        Button sendEmail_button = findViewById(R.id.SendEmail_button);

        //Back arrow button
        backArrow_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class))
        );

        //Home button
        home_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, MainMenu.class))
        );

        //Send email button
        sendEmail_button.setOnClickListener(v -> {
            String email = email_editText.getText().toString().trim();

            //Change text colors if required fields aren't filed
            if (email.isEmpty()) {
                changeColors(email_editText, email_textView, "#f32c1e");
                Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                changeColors(email_editText, email_textView, "#ffffffff");

                firebaseAuth.sendPasswordResetEmail(email_editText.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                                Toast.makeText(getApplicationContext(), "Email was sent successfully", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                        });

                startActivity(new Intent(this, Login.class));
            }
        });
    }

    //Function changes the colors of the editText tile text and lines
    private void changeColors(EditText et, TextView tv, String color){
        ViewCompat.setBackgroundTintList(et, ColorStateList.valueOf(Color.parseColor(color)));
        tv.setTextColor(Color.parseColor(color));
    }
}