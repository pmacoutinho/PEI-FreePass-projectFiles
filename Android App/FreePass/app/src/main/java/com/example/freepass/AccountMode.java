package com.example.freepass;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.widget.CompoundButtonCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

    //TODO: [MEDIUM PRIORITY] ENCRYPT WEBSITE_URL AND USERNAME

public class AccountMode extends AppCompatActivity {

    FirebaseFirestore firebaseStore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_mode);

        //Hides the action bar (bar on top)
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Resets the workbench on startup
        saveWorkbench("");

        List<String> savedWebsiteURL = new ArrayList<>();
        CollectionReference collectionReference = firebaseStore.collection(userID);
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult()))
                    savedWebsiteURL.add(document.getId());
                Log.d("Saved website list", "Saved website list retrieved successfully: " + savedWebsiteURL.toString());
            } else
                Log.d("Saved website list", "Error: ", task.getException());
        });

        ImageView settings_imageView = findViewById(R.id.Settings_imageView);
        ImageView info_imageView = findViewById(R.id.Info_imageView);
        ImageView home_imageView = findViewById(R.id.Home_imageView);
        TextView websiteURL_textView = findViewById((R.id.WebsiteURL_textView));
        AutoCompleteTextView websiteURL_editText = findViewById((R.id.WebsiteURL_editText));
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
        ImageView save_imageView = findViewById(R.id.Save_imageView);
        TextView save_textView = findViewById(R.id.Save_textView);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        username_editText.setText(sharedPreferences.getString("username_default", ""));
        lowerCase_checkBox.setChecked(sharedPreferences.getBoolean("lowercase_default", true));
        upperCase_checkBox.setChecked(sharedPreferences.getBoolean("uppercase_default", true));
        number_checkBox.setChecked(sharedPreferences.getBoolean("number_default", true));
        symbol_checkBox.setChecked(sharedPreferences.getBoolean("symbol_default", true));
        length_editText.setText(sharedPreferences.getString("length_default", "16"));
        counter_editText.setText(sharedPreferences.getString("counter_default", "1"));

        //Settings button
        settings_imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, Settings.class);
            intent.putExtra("loggedIn", "true");
            intent.putExtra("mode", "account");
            startActivity(intent);
        });

        //Info button
        info_imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, Info.class);
            intent.putExtra("mode", "account");
            startActivity(intent);
        });

        //Home button
        home_imageView.setOnClickListener(v ->
                startActivity(new Intent(this, MainMenu.class))
        );

        resetTextChange(websiteURL_editText);
        resetTextChange(username_editText);
        resetTextChange(masterPassword_editText);
        resetTextChange(length_editText);
        resetTextChange(counter_editText);

        lowerCase_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxListener());
        upperCase_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxListener());
        number_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxListener());
        symbol_checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkboxListener());

        length_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!length_editText.getText().toString().equals(""))
                    if (Integer.parseInt(length_editText.getText().toString()) < 8
                            || Integer.parseInt(length_editText.getText().toString()) > 128)
                        changeColorsAndText(length_editText, length_textView, "#f32c1e");
                    else
                        changeColorsAndText(length_editText, length_textView, "#ffffff");
            }
            //These function need to exist within TextWatcher()
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        counter_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!counter_editText.getText().toString().equals(""))
                    if (Integer.parseInt(counter_editText.getText().toString()) < 1)
                        changeColorsAndText(counter_editText, counter_textView, "#f32c1e");
                    else
                        changeColorsAndText(counter_editText, counter_textView, "#ffffff");
            }
            //These function need to exist within TextWatcher()
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        //Dropdown menu for websiteURL
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, savedWebsiteURL);
        websiteURL_editText.setAdapter(adapter);

        //Fill fields if user clicks on the websiteURL_editText suggestion
        websiteURL_editText.setOnItemClickListener((p, v, pos, id) -> {
            DocumentReference documentReference = firebaseStore.collection(userID).document(p.getItemAtPosition(pos).toString());
            documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
                assert documentSnapshot != null;
                username_editText.setText(documentSnapshot.getString("username"));
                lowerCase_checkBox.setChecked(Objects.requireNonNull(documentSnapshot.getBoolean("lowercase")).equals(true));
                upperCase_checkBox.setChecked(Objects.requireNonNull(documentSnapshot.getBoolean("uppercase")).equals(true));
                number_checkBox.setChecked(Objects.requireNonNull(documentSnapshot.getBoolean("number")).equals(true));
                symbol_checkBox.setChecked(Objects.requireNonNull(documentSnapshot.getBoolean("symbol")).equals(true));
                length_editText.setText(documentSnapshot.getString("length"));
                counter_editText.setText(documentSnapshot.getString("counter"));
            });
        });

        //Add workbench button
        //IMPORTANT: Workbench should always be trimmed for consistency's sake
        workbench_button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View workbench_layout = getLayoutInflater().inflate(R.layout.layout_workbench, null);
            EditText workbench_editText = workbench_layout.findViewById(R.id.Workbench_editText);

            if (!genPassword_button.getText().toString().matches("Generate Password"))
                resetPasswordGeneration();

            builder.setView(workbench_layout);

            builder.setPositiveButton("Add", (dialog, id) -> {
                if (workbench_editText.getText().toString().length() <= 10000) {
                    saveWorkbench(workbench_editText.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "Workbench added successfully", Toast.LENGTH_SHORT).show();
                    resetPasswordGeneration();
                } else
                    Toast.makeText(getApplicationContext(), "Character limit is 10.000", Toast.LENGTH_SHORT).show();
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
                    save_imageView.setVisibility(View.VISIBLE);
                    save_textView.setVisibility(View.VISIBLE);
                } catch (NoSuchAlgorithmException e) { e.printStackTrace();
                } catch (InvalidKeySpecException e) { e.printStackTrace(); }
            }
        });

        //Show/Hide password button
        show_imageView.setOnClickListener(v -> view());
        show_textView.setOnClickListener(v -> view());

        //Copy password to clipboard button
        copyClipboard_imageView.setOnClickListener(v -> copyClipboard());
        copyClipboard_textView.setOnClickListener(v -> copyClipboard());

        //Reset every field button
        reset_imageView.setOnClickListener(v -> reset());
        reset_textView.setOnClickListener(v -> reset());

        save_imageView.setOnClickListener(v -> save());
        save_textView.setOnClickListener(v -> save());
    }

    //All checkbox listeners call this function
    private void checkboxListener() {
        CheckBox lowerCase_checkBox = findViewById(R.id.LowerCase_checkBox);
        CheckBox upperCase_checkBox = findViewById(R.id.UpperCase_checkBox);
        CheckBox number_checkBox = findViewById(R.id.Number_checkBox);
        CheckBox symbol_checkBox = findViewById(R.id.Symbol_checkBox);

        resetPasswordGeneration();

        if (!lowerCase_checkBox.isChecked() && !upperCase_checkBox.isChecked() && !number_checkBox.isChecked() && !symbol_checkBox.isChecked()) {
            changeCheckboxColor(lowerCase_checkBox, "#f32c1e");
            changeCheckboxColor(upperCase_checkBox, "#f32c1e");
            changeCheckboxColor(number_checkBox, "#f32c1e");
            changeCheckboxColor(symbol_checkBox, "#f32c1e");
        } else {
            changeCheckboxColor(lowerCase_checkBox, "#FFFFFF");
            changeCheckboxColor(upperCase_checkBox, "#FFFFFF");
            changeCheckboxColor(number_checkBox, "#FFFFFF");
            changeCheckboxColor(symbol_checkBox, "#FFFFFF");
        }
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

    private void changeCheckboxColor(CheckBox cb, String color) {
        if (Build.VERSION.SDK_INT < 21)
            CompoundButtonCompat.setButtonTintList(cb, ColorStateList.valueOf(Color.parseColor(color)));
        else
            cb.setButtonTintList(ColorStateList.valueOf(Color.parseColor(color)));

        cb.setTextColor(Color.parseColor(color));
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

    //Function copies the generated to clipboard
    private void copyClipboard() {
        Button genPassword_button = findViewById(R.id.GenPassword_button);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData genPassword_clipboard = ClipData.newPlainText("genPassword_editText", genPassword_button.getText());
        clipboard.setPrimaryClip(genPassword_clipboard);
        Toast.makeText(getApplicationContext(), "Password copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    //Function resets all the editText fields and workbench, it also calls resetPasswordGeneration()
    private void reset() {
        AutoCompleteTextView websiteURL_editText = findViewById((R.id.WebsiteURL_editText));
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
        ImageView save_imageView = findViewById(R.id.Save_imageView);
        TextView save_textView = findViewById(R.id.Save_textView);

        //R.string.generate_password_caps = GENERATE PASSWORD
        genPassword_button.setText(this.getString(R.string.generate_password_caps));
        genPassword_button.setInputType(InputType.TYPE_NULL);
        copyClipboard_imageView.setVisibility(View.GONE);
        copyClipboard_textView.setVisibility(View.GONE);
        show_imageView.setVisibility(View.GONE);
        show_textView.setVisibility(View.GONE);
        reset_imageView.setVisibility(View.GONE);
        reset_textView.setVisibility(View.GONE);
        save_imageView.setVisibility(View.GONE);
        save_textView.setVisibility(View.GONE);
    }

    private void save() {
        AutoCompleteTextView websiteURL_editText = findViewById((R.id.WebsiteURL_editText));
        EditText username_editText = findViewById((R.id.Username_editText));
        CheckBox lowerCase_checkBox = findViewById(R.id.LowerCase_checkBox);
        CheckBox upperCase_checkBox = findViewById(R.id.UpperCase_checkBox);
        CheckBox number_checkBox = findViewById(R.id.Number_checkBox);
        CheckBox symbol_checkBox = findViewById(R.id.Symbol_checkBox);
        EditText length_editText = findViewById((R.id.Length_editText));
        EditText counter_editText = findViewById((R.id.Counter_editText));

        String websiteURL = websiteURL_editText.getText().toString().trim();
        String username = username_editText.getText().toString().trim();
        String length = length_editText.getText().toString().trim();
        String counter = counter_editText.getText().toString().trim();

        String workbench = getApplicationContext().getSharedPreferences("WORKBENCH", 0).getString("Workbench", null);
        if (workbench.matches("")) //If the user didn't define a workbench, a trimmed version of the first paragraph of 1984 by George Orwell is used
            workbench = "It was a bright cold day in April, and the clocks were striking thirteen. Winston Smith, his chin nuzzled into his breast in an effort to escape the vile wind, slipped quickly through the glass doors of Victory Mansions, though not quickly enough to prevent a swirl of gritty dust from entering along with him. ".trim();
        Log.i("workbench", "Workbench: " + workbench);

        DocumentReference documentReference = firebaseStore.collection(userID).document(websiteURL);

        Map<String, Object> data = new HashMap<>();
        data.put("userID", userID);
        data.put("websiteURL", websiteURL);
        data.put("username", username);
        data.put("lowercase", lowerCase_checkBox.isChecked());
        data.put("uppercase", upperCase_checkBox.isChecked());
        data.put("number", number_checkBox.isChecked());
        data.put("symbol", symbol_checkBox.isChecked());
        data.put("length", length);
        data.put("counter", counter);

        documentReference.set(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("Save Successful", "Save Successful: user_data profile was created for user: " + userID);
                Toast.makeText(getApplicationContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("Save Error", "Error: " + Objects.requireNonNull(task.getException()).getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });

    }
}
