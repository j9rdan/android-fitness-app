package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserLogin extends AppCompatActivity implements View.OnClickListener {

    private EditText username_et, password_et;
    private Button loginBtn;
    private TextView forgotPassword, signUpOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        username_et = findViewById(R.id.username_login_input);
        password_et = findViewById(R.id.password_login_input);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPWOption);
        signUpOption = findViewById(R.id.regOption);

        loginBtn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUpOption.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                signUserIn();
                break;
            case R.id.forgotPWOption:
                resetPassword();
                break;
            case R.id.regOption:
                startActivity(new Intent(this, UserSignup.class));
        }

    }

    private void resetPassword() {
    }

    private void signUserIn() {
        // store inputs as strings
        String username = username_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        // input validation
        if (username.isEmpty()) {
            username_et.setError("Enter a username");
            username_et.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_et.setError("Enter a password");
            password_et.requestFocus();
        }

    }
}