package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Register extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        ImageView home_imageView = findViewById(R.id.Home_imageView);
        TextView email_textView = findViewById((R.id.Email_textView));
        EditText email_editText = findViewById(R.id.Email_editText);
        TextView password_textView = findViewById((R.id.Password_textView));
        EditText password_editText = findViewById(R.id.Password_editText);
        ImageView viewPassword_imageView = findViewById(R.id.ViewPassword_imageView);
        TextView confirmPassword_textView = findViewById((R.id.ConfirmPassword_TextView));
        EditText confirmPassword_editText = findViewById(R.id.ConfirmPassword_editText);
        ImageView viewConfirmPassword_imageView = findViewById(R.id.ViewConfirmPassword_imageView);
        TextView login_textView = findViewById((R.id.Login_textView));
        Button createAccount_button = findViewById(R.id.CreateAccount_button);
        Button cancel_button = findViewById(R.id.Cancel_button);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        //Home button
        home_imageView.setOnClickListener(v ->
            startActivity(new Intent(this, MainMenu.class))
        );

        //Show/Hide password button
        viewPassword_imageView.setOnClickListener(v -> {
            if (password_editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                //Show password
                password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                //Hide password
                password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        //Show/Hide confirm password button
        viewConfirmPassword_imageView.setOnClickListener(v -> {
            if (confirmPassword_editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                //Show password
                confirmPassword_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                //Hide password
                confirmPassword_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        //Log in textView
        login_textView.setOnClickListener(v ->
            startActivity(new Intent(this, Login.class))
        );

        createAccount_button.setOnClickListener(v -> {
            String email = email_editText.getText().toString().trim();
            String password = password_editText.getText().toString().trim();
            String confirmPassword = confirmPassword_editText.getText().toString().trim();

            //Change text colors if required fields aren't filed
            if (email.isEmpty())
                changeColors(email_editText, email_textView, "#f32c1e");
            else
                changeColors(email_editText, email_textView, "#ffffffff");

            if (password.isEmpty())
                changeColors(password_editText, password_textView, "#f32c1e");
            //It shouldn't reveal the passwords don't much before all the fields are filled
            else if (!email.isEmpty() && !confirmPassword.isEmpty() && !password.matches(confirmPassword))
                changeColors(password_editText, password_textView, "#f32c1e");
            else
                changeColors(password_editText, password_textView, "#ffffffff");

            if (confirmPassword.isEmpty())
                changeColors(confirmPassword_editText, confirmPassword_textView, "#f32c1e");
            //It shouldn't reveal the passwords don't much before all the fields are filled
            else if (!email.isEmpty() && !password.isEmpty() && !password.matches(confirmPassword))
                changeColors(confirmPassword_editText, confirmPassword_editText, "#f32c1e");
            else
                changeColors(confirmPassword_editText, confirmPassword_textView, "#ffffffff");

            if (password.length() < 6)
                changeColors(password_editText, password_textView, "#f32c1e");
            else
                changeColors(password_editText, password_textView, "#ffffffff");

            //--- The next "if"'s are here and not in the color changes in order to prevent multiple Toasts showing at the same time
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
                Toast.makeText(getApplicationContext(), "Fill the required fields", Toast.LENGTH_SHORT).show();
            else if (password.length() < 6)
                Toast.makeText(getApplicationContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            else if (!password.matches(confirmPassword))
                Toast.makeText(getApplicationContext(), "Password confirmation does not match", Toast.LENGTH_SHORT).show();
            else {
                progressBar.setVisibility(View.VISIBLE);
                //Changing progressBar color is different for pre-lollipop android versions
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawableProgress = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                    DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(this, android.R.color.white));
                    progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));

                } else
                    progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_IN);

                createAccount(email, password);
            }
        });

        cancel_button.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class))
        );
    }

    //Function changes the colors of the editText tile text and lines
    private void changeColors(EditText et, TextView tv, String color) {
        ViewCompat.setBackgroundTintList(et, ColorStateList.valueOf(Color.parseColor(color)));
        tv.setTextColor(Color.parseColor(color));
    }

    private void createAccount(String email, String password) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task_createUser -> {
            if (task_createUser.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                user.sendEmailVerification()
                        .addOnCompleteListener(this, task_sendEmailVerification -> {
                            if (task_sendEmailVerification.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Check email to verify account", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            } else
                                Toast.makeText(getApplicationContext(),
                                        "Error: " + Objects.requireNonNull(task_sendEmailVerification.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else
                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task_createUser.getException()).getMessage(),
                        Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);
        });
    }
}
