package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button regBtn;
    private EditText username_et, password_et, email_et;
    private TextView loginTxtBtn;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise all activity components
        regBtn = findViewById(R.id.button);
        username_et = findViewById(R.id.username_reg_input);
        password_et = findViewById(R.id.password_reg_input);
        email_et = findViewById(R.id.email_reg_input);
        loginTxtBtn = findViewById(R.id.loginOption);
        progressBar = findViewById(R.id.progressBar);

        // set click listeners for all clickable components
        regBtn.setOnClickListener(this);
        loginTxtBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                registerUser();
//                startActivity(new Intent(this, SelectProgram.class));
                break;
            case R.id.loginOption:
                startActivity(new Intent(this, UserLogin.class));
                break;
        }
    }

    private void registerUser() {

        // store inputs as strings
        String username = username_et.getText().toString().trim();
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        // input validation
        if (username.isEmpty()) {
            username_et.setError("Username required");
            username_et.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            email_et.setError("Email required");
            email_et.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // ensure correct email format
            email_et.setError("Invalid email");
            email_et.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_et.setError("Password required");
            password_et.requestFocus();
            return;
        }

        if (password.length() < 6) { // firebase passwords do not accept < 6 chars
            password_et.setError("Passwords must be at least 6 characters long");
            password_et.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);

        // write object user to firebase db
    }
}