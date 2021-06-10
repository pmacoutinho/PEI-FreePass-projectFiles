package com.example.freepass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

    //TODO: [MEDIUM PRIORITY] WORKBENCH CHARACTER LIMIT
    //TODO: [LOW PRIORITY] CHANGE CHECKBOX COLOR WHEN NONE OF THEM IS CHECKED AND WHEN THEY'RE CHECKED
    //TODO: [LOW PRIORITY] MAYBE VERIFY IF LENGTH AND COUNTER ARE INVALID *BEFORE* PRESSING THE GENERATE BUTTON
    //TODO: [LOW PRIORITY] MAKE APP COMPATIBLE WITH ALL SCREEN SIZES
    //TODO: [LOW PRIORITY] FIX WARNINGS

public class AnonymousMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_mode);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Resets the workbench on startup
        saveWorkbench("");

        ImageView settings_imageView = findViewById(R.id.Settings_imageView);
        ImageView home_imageView = findViewById(R.id.Home_imageView);
        ImageView info_imageView = findViewById(R.id.Info_imageView);
        TextView websiteURL_textView = findViewById((R.id.WebsiteURL_textView));
        EditText websiteURL_editText = findViewById((R.id.WebsiteURL_editText));
        TextView username_textView = findViewById((R.id.Username_textView));
        EditText username_editText = findViewById((R.id.Username_editText));
        TextView masterPassword_textView = findViewById((R.id.MasterPassword_textView));
        EditText masterPassword_editText = findViewById((R.id.MasterPassword_editText));
        CheckBox lowerCase_checkBox = findViewById(R.id.LowerCase_checkBox);
        CheckBox upperCase_checkBox = findViewById(R.id.UpperCase_checkBox);
        CheckBox number_checkBox = findViewById(R.id.Number_checkBox);
        CheckBox symbol_checkBox = findViewById(R.id.Symbol_checkBox);
        TextView length_textView = findViewById(R.id.Length_textView);
        EditText length_editText = findViewById(R.id.Length_editText);
        TextView counter_textView = findViewById(R.id.Counter_textView);
        EditText counter_editText = findViewById(R.id.Counter_editText);
        Button workbench_button = findViewById(R.id.Workbench_button);
        Button genPassword_button = findViewById(R.id.GenPassword_button);
        ImageView copyClipboard_imageView = findViewById(R.id.CopyClipboard_imageView);
        TextView copyClipboard_textView = findViewById(R.id.CopyClipboard_textView);
        ImageView show_imageView = findViewById(R.id.Show_imageView);
        TextView show_textView = findViewById(R.id.Show_textView);
        ImageView reset_imageView = findViewById(R.id.Reset_imageView);
        TextView reset_textView = findViewById(R.id.Reset_textView);

        //Settings button
        settings_imageView.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            Intent intent = new Intent(this, Settings.class);
            intent.putExtra("mode", "anonymous");

            if (firebaseAuth.getCurrentUser() != null)
                intent.putExtra("loggedIn", "true");
            else
                intent.putExtra("loggedIn", "false");

            startActivity(intent);
        });

        //Info button
        info_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, Info.class))
        );

        //Home button
        home_imageView.setOnClickListener(v ->
            startActivity(new Intent(this, MainMenu.class))
        );

        resetTextChange(websiteURL_editText);
        resetTextChange(username_editText);
        resetTextChange(masterPassword_editText);
        resetTextChange(length_editText);
        resetTextChange(counter_editText);

        //Add workbench button
        workbench_button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View workbench_layout = getLayoutInflater().inflate(R.layout.layout_workbench, null);
            EditText workbench_editText = workbench_layout.findViewById(R.id.Workbench_editText);

            builder.setView(workbench_layout);
            //builder.setTitle("Add Workbench");

            builder.setPositiveButton("Add", (dialog, id) -> {
                saveWorkbench(workbench_editText.getText().toString().trim());
                Toast.makeText(getApplicationContext(), "Workbench added successfully", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Cancel", (dialog, id) -> {});

            AlertDialog dialog = builder.create();
            dialog.show();

            //Change button and window colors
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#36393f")));
        });

        genPassword_button.setOnClickListener(v -> {
            String websiteURL = websiteURL_editText.getText().toString().trim();
            String username = username_editText.getText().toString().trim();
            String masterPassword = masterPassword_editText.getText().toString().trim();
            String length = length_editText.getText().toString().trim();
            String counter = counter_editText.getText().toString().trim();

            //Change text colors if required fields aren't filed
            if (websiteURL.isEmpty())
                changeColors(websiteURL_editText, websiteURL_textView, "#f32c1e");
            else
                changeColors(websiteURL_editText, websiteURL_textView, "#ffffffff");

            if (username.isEmpty())
                changeColors(username_editText, username_textView, "#f32c1e");
            else
                changeColors(username_editText, username_textView, "#ffffffff");

            if (masterPassword.isEmpty())
                changeColors(masterPassword_editText, masterPassword_textView, "#f32c1e");
            else
                changeColors(masterPassword_editText, masterPassword_textView, "#ffffffff");

            if (length.isEmpty() || Integer.parseInt(length) < 8)
                changeColorsAndText(length_editText, length_textView, "#f32c1e");
            else if (Integer.parseInt(length) > 128)
                changeColorsAndText(length_editText, length_textView, "#f32c1e");
            else
                changeColorsAndText(length_editText, length_textView, "#ffffffff");

            if (counter.isEmpty() || Integer.parseInt(counter) < 1)
                changeColorsAndText(counter_editText, counter_textView, "#f32c1e");
            else
                changeColorsAndText(counter_editText, counter_textView, "#ffffffff");

            /*
            //Change checkboxes colors if they're not all checked
            if (!lowerCase_checkBox.isChecked() && !upperCase_checkBox.isChecked() && !number_checkBox.isChecked()
                    && !symbol_checkBox.isChecked()) {
                upperCase_checkBox.setBackgroundColor(Color.parseColor("#f32c1e"));
                number_checkBox.setBackgroundColor(Color.parseColor("#f32c1e"));
                symbol_checkBox.setBackgroundColor(Color.parseColor("#f32c1e"));
            }*/

            //If the required fields aren't filled the password is not generated
            if (websiteURL.isEmpty() || username.isEmpty() || masterPassword.isEmpty())
                Toast.makeText(getApplicationContext(), "Fill the required fields", Toast.LENGTH_SHORT).show();
            //If all the checkboxes are unchecked the password is not generated
            else if (!lowerCase_checkBox.isChecked() && !upperCase_checkBox.isChecked() && !number_checkBox.isChecked() && !symbol_checkBox.isChecked())
                Toast.makeText(getApplicationContext(), "Check at least one checkbox", Toast.LENGTH_SHORT).show();
            //--- The next three "else if" are here and not in the color changes in order to prevent multiple Toasts showing at the same time
            else if (length.isEmpty() || Integer.parseInt(length) < 8)
                Toast.makeText(getApplicationContext(), "Minimum password length is 8", Toast.LENGTH_SHORT).show();
            else if (Integer.parseInt(length) > 128)
                Toast.makeText(getApplicationContext(), "Maximum password length is 128", Toast.LENGTH_SHORT).show();
            else if (counter.isEmpty() || Integer.parseInt(counter) < 1)
                Toast.makeText(getApplicationContext(), "Minimum counter value is 1", Toast.LENGTH_SHORT).show();
            //---
            //If everything was filled correctly the password can be generated
            else {
                //Load the workbench
                String workbench = getApplicationContext().getSharedPreferences("WORKBENCH", 0).getString("Workbench", null);
                if (workbench.matches("")) //If the user didn't define a workbench, a trimmed version of the first paragraph of 1984 by George Orwell is used
                    workbench = "It was a bright cold day in April, and the clocks were striking thirteen. Winston Smith, his chin nuzzled into his breast in an effort to escape the vile wind, slipped quickly through the glass doors of Victory Mansions, though not quickly enough to prevent a swirl of gritty dust from entering along with him. ".trim();
                Log.i("workbench", "wb: " + workbench);
                //****************************************** GENERATE THE PASSWORD ******************************************\\
                try {
                    genPassword_button.setText(PasswordGeneration.main(websiteURL, username, masterPassword, Integer.parseInt(length), Integer.parseInt(counter),
                            workbench, lowerCase_checkBox.isChecked(), upperCase_checkBox.isChecked(), number_checkBox.isChecked(), symbol_checkBox.isChecked()));

                    genPassword_button.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    copyClipboard_imageView.setVisibility(View.VISIBLE);
                    copyClipboard_textView.setVisibility(View.VISIBLE);
                    show_imageView.setVisibility(View.VISIBLE);
                    show_textView.setVisibility(View.VISIBLE);
                    reset_imageView.setVisibility(View.VISIBLE);
                    reset_textView.setVisibility(View.VISIBLE);
                } catch (NoSuchAlgorithmException e) { e.printStackTrace();
                } catch (InvalidKeySpecException e) { e.printStackTrace(); }
            }
        });

        //Copy password to clipboard button
        copyClipboard_imageView.setOnClickListener(v -> copyClipboard());
        copyClipboard_textView.setOnClickListener(v -> copyClipboard());

        //Show/Hide password button
        show_imageView.setOnClickListener(v -> view());
        show_textView.setOnClickListener(v -> view());

        //Reset every field button
        reset_imageView.setOnClickListener(v -> reset());
        reset_textView.setOnClickListener(v -> reset());
    }

    //Function saves the workbench entered in the dialog window
    private void saveWorkbench(String workbench) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("WORKBENCH", 0);
        SharedPreferences.Editor prefEdit = pref.edit();
        prefEdit.putString("Workbench", workbench);
        prefEdit.apply();
    }

    //Function changes the colors of the editText tile text and lines
    private void changeColors(EditText et, TextView tv, String color) {
        ViewCompat.setBackgroundTintList(et, ColorStateList.valueOf(Color.parseColor(color)));
        tv.setTextColor(Color.parseColor(color));
    }

    //Function changes the colors of the editText tile text, lines, and the text itself
    private void changeColorsAndText(EditText et, TextView tv, String color) {
        ViewCompat.setBackgroundTintList(et, ColorStateList.valueOf(Color.parseColor(color)));
        et.setTextColor(Color.parseColor(color));
        tv.setTextColor(Color.parseColor(color));
    }

    //Calls the reset function when there's a change in the provided editText
    private void resetTextChange(EditText et) {
        Button genPassword_button = findViewById(R.id.GenPassword_button);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!genPassword_button.getText().toString().matches("Generate Password"))
                    resetPasswordGeneration();
            }
            //These function need to exist within TextWatcher()
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }

    //Function copies the generated to clipboard
    private void copyClipboard() {
        Button genPassword_button = findViewById(R.id.GenPassword_button);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData genPassword_clipboard = ClipData.newPlainText("genPassword_editText", genPassword_button.getText());
        clipboard.setPrimaryClip(genPassword_clipboard);
        Toast.makeText(getApplicationContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    //Function reveals the generated password to the user
    private void view() {
        Button genPassword_button = findViewById(R.id.GenPassword_button);

        if (genPassword_button.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
            //Show password
            genPassword_button.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            //Hide password
            genPassword_button.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    //Function resets all the editText fields and workbench, it also calls resetPasswordGeneration()
    private void reset() {
        EditText websiteURL_editText = findViewById((R.id.WebsiteURL_editText));
        EditText username_editText = findViewById((R.id.Username_editText));
        EditText masterPassword_editText = findViewById((R.id.MasterPassword_editText));

        websiteURL_editText.setText("");
        username_editText.setText("");
        masterPassword_editText.setText("");
        saveWorkbench("");
        resetPasswordGeneration();
    }

    //Resets all the changes after the "Generate Password" button is clicked
    private void resetPasswordGeneration() {
        Button genPassword_button = findViewById(R.id.GenPassword_button);
        ImageView copyClipboard_imageView = findViewById(R.id.CopyClipboard_imageView);
        TextView copyClipboard_textView = findViewById(R.id.CopyClipboard_textView);
        ImageView show_imageView = findViewById(R.id.Show_imageView);
        TextView show_textView = findViewById(R.id.Show_textView);
        ImageView reset_imageView = findViewById(R.id.Reset_imageView);
        TextView reset_textView = findViewById(R.id.Reset_textView);

        genPassword_button.setText("GENERATE PASSWORD");
        genPassword_button.setInputType(InputType.TYPE_NULL);
        copyClipboard_imageView.setVisibility(View.GONE);
        copyClipboard_textView.setVisibility(View.GONE);
        show_imageView.setVisibility(View.GONE);
        show_textView.setVisibility(View.GONE);
        reset_imageView.setVisibility(View.GONE);
        reset_textView.setVisibility(View.GONE);
    }
}
