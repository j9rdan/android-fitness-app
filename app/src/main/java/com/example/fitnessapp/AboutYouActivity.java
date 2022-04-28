package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AboutYouActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_sex, btn_next;
    private EditText birthdate_et, height_et, weight_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_you);

        // initialise activity components & set click listeners

        btn_sex = findViewById(R.id.sex_toggle);
        btn_sex.setOnClickListener(this);

        btn_next = findViewById(R.id.btn_next_aboutYou);
        btn_next.setOnClickListener(this);

        birthdate_et = findViewById(R.id.birthdate_field);
        height_et = findViewById(R.id.height_field);
        weight_et = findViewById(R.id.weight_field);

    }

    @Override
    public void onClick(View view) {

        String birthdate_str = birthdate_et.getText().toString().trim();

        if (btn_next.isPressed()) {

            // input validation
            if (birthdate_str.isEmpty()) {
                birthdate_et.setError("Enter a date of birth");
                birthdate_et.requestFocus();
                return;
            } else if (height_et.getText().toString().trim().isEmpty()) {
                height_et.setError("Enter a height");
                height_et.requestFocus();
                return;
            } else if (weight_et.getText().toString().trim().isEmpty()) {
                weight_et.setError("Enter a weight");
                weight_et.requestFocus();
                return;
            }
        }

    }
}