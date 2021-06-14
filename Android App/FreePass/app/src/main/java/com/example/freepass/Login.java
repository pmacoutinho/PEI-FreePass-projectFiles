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

import java.util.Objects;

    //TODO: [LOW PRIORITY] IMPLEMENT 2FA

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Log out user just in case they're signed in
        firebaseAuth.signOut();

        ImageView home_imageView = findViewById(R.id.Home_imageView);
        TextView email_textView = findViewById((R.id.Email_textView));
        EditText email_editText = findViewById(R.id.Email_editText);
        TextView password_textView = findViewById((R.id.Password_textView));
        EditText password_editText = findViewById(R.id.Password_editText);
        ImageView view_imageView = findViewById(R.id.View_imageView);
        TextView forgotPassword_textView = findViewById((R.id.ForgotPassword_textView));
        Button login_button = findViewById(R.id.Login_button);
        Button createAccount_button = findViewById(R.id.CreateAccount_button);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        //Home button
        home_imageView.setOnClickListener(v ->
            startActivity(new Intent(this, MainMenu.class))
        );

        //Show/Hide password button
        view_imageView.setOnClickListener(v -> {
            if (password_editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                //Show password
                password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                //Hide password
                password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        //Forgot Password
        forgotPassword_textView.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPassword.class))
        );

        login_button.setOnClickListener(v -> {
            String email = email_editText.getText().toString().trim();
            String password = password_editText.getText().toString().trim();

            //Change text colors if required fields aren't filed
            if (email.isEmpty())
                changeColors(email_editText, email_textView, "#f32c1e");
            else
                changeColors(email_editText, email_textView, "#ffffffff");

            if (password.isEmpty())
                changeColors(password_editText, password_textView, "#f32c1e");
            else
                changeColors(password_editText, password_textView, "#ffffffff");

            //--- The next "if"'s are here and not in the color changes in order to prevent multiple Toasts showing at the same time
            if (email.isEmpty() || password.isEmpty())
                Toast.makeText(this, "Fill the required fields", Toast.LENGTH_SHORT).show();
            else {
                progressBar.setVisibility(View.VISIBLE);
                //Changing progressBar color is different for pre-lollipop android versions
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawableProgress = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                    DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(this, android.R.color.white));
                    progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));

                } else
                    progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_IN);

                login(email, password);
            }
        });

        createAccount_button.setOnClickListener(v ->
                startActivity(new Intent(this, Register.class))
        );
    }

    //Function changes the colors of the editText tile text and lines
    private void changeColors(EditText et, TextView tv, String color){
        ViewCompat.setBackgroundTintList(et, ColorStateList.valueOf(Color.parseColor(color)));
        tv.setTextColor(Color.parseColor(color));
    }

    private void login(String email, String password) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseAuth.getCurrentUser().reload();
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AccountMode.class));
                } else
                    Toast.makeText(this, "Please verify your email first", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(),
                        Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);
        });
    }
}
