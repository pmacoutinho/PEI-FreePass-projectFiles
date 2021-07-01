package com.example.freepass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    //TODO: [HIGH PRIORITY] DELETE ACCOUNT

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView home_imageView = findViewById(R.id.Home_imageView);
        ImageView backArrow_imageView = findViewById(R.id.BackArrow_imageView);
        EditText username_editText = findViewById(R.id.Username_editText);
        CheckBox lowerCase_checkBox = findViewById(R.id.LowerCase_checkBox);
        CheckBox upperCase_checkBox = findViewById(R.id.UpperCase_checkBox);
        CheckBox number_checkBox = findViewById(R.id.Number_checkBox);
        CheckBox symbol_checkBox = findViewById(R.id.Symbol_checkBox);
        EditText length_editText = findViewById(R.id.Length_editText);
        EditText counter_editText = findViewById(R.id.Counter_editText);
        EditText minNumbers_editText = findViewById(R.id.MinNumbers_editText);
        EditText minSymbols_editText = findViewById(R.id.MinSymbols_editText);
        Button save_button = findViewById(R.id.Save_button);
        TextView loggedInAs_textView = findViewById(R.id.LoggedInAs_textView);
        Button logout_button = findViewById(R.id.Logout_button);
        Button deleteAccount_button = findViewById(R.id.DeleteAccount_button);
        TextView notLogged_textView = findViewById(R.id.NotLogged_textView);
        Button login_button = findViewById(R.id.Login_button);
        Button register_button = findViewById(R.id.Register_button);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        username_editText.setText(sharedPreferences.getString("username_default", ""));
        lowerCase_checkBox.setChecked(sharedPreferences.getBoolean("lowercase_default", true));
        upperCase_checkBox.setChecked(sharedPreferences.getBoolean("uppercase_default", true));
        number_checkBox.setChecked(sharedPreferences.getBoolean("number_default", true));
        symbol_checkBox.setChecked(sharedPreferences.getBoolean("symbol_default", true));
        length_editText.setText(sharedPreferences.getString("length_default", "16"));
        counter_editText.setText(sharedPreferences.getString("counter_default", "1"));
        counter_editText.setText(sharedPreferences.getString("min_number", "2"));
        counter_editText.setText(sharedPreferences.getString("min_symbol", "2"));

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        if (getIntent().getStringExtra("loggedIn").matches("true")) {
            loggedInAs_textView.setVisibility(View.VISIBLE);
            logout_button.setVisibility(View.VISIBLE);
            deleteAccount_button.setVisibility(View.VISIBLE);
            notLogged_textView.setVisibility(View.GONE);
            login_button.setVisibility(View.GONE);
            register_button.setVisibility(View.GONE);
            loggedInAs_textView.setText(String.format("Logged in as: %s",
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()));
        } else {
            loggedInAs_textView.setVisibility(View.GONE);
            logout_button.setVisibility(View.GONE);
            deleteAccount_button.setVisibility(View.GONE);
            notLogged_textView.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.VISIBLE);
            register_button.setVisibility(View.VISIBLE);
        }

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

        save_button.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username_default", username_editText.getText().toString());
            editor.putBoolean("lowercase_default", lowerCase_checkBox.isChecked());
            editor.putBoolean("uppercase_default", upperCase_checkBox.isChecked());
            editor.putBoolean("number_default", number_checkBox.isChecked());
            editor.putBoolean("symbol_default", symbol_checkBox.isChecked());
            editor.putString("length_default", length_editText.getText().toString());
            editor.putString("counter_default", counter_editText.getText().toString());
            editor.putString("min_number", minNumbers_editText.getText().toString());
            editor.putString("min_symbol", minSymbols_editText.getText().toString());
            editor.apply();
            Toast.makeText(this, "Defaults saved", Toast.LENGTH_SHORT).show();
        });

        logout_button.setOnClickListener(v -> {
            firebaseAuth.signOut();
            if (getIntent().getStringExtra("mode").matches("account"))
                startActivity(new Intent(this, Login.class));
            else
                startActivity(new Intent(this, AnonymousMode.class));
        });

        deleteAccount_button.setOnClickListener(v -> {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

            builder.setMessage("Please confirm that you wish to delete your account.")
                    .setPositiveButton("Confirm", (dialog, id) ->
                            Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_SHORT).show())

                    .setNegativeButton("Cancel", (dialog, id) -> {});

            AlertDialog alert = builder.create();
            alert.show();

            //Change button and window colors
            alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            alert.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36393f")));
        });

        login_button.setOnClickListener(v ->
                startActivity(new Intent(this, Login.class))
        );

        register_button.setOnClickListener(v ->
                startActivity(new Intent(this, Register.class))
        );
    }
}
