package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button regBtn;
    private EditText username_et, password_et, email_et;
    private TextView loginTxtBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // get an instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // initialise all activity components
        regBtn = findViewById(R.id.button);
        username_et = findViewById(R.id.username_reg_input);
        password_et = findViewById(R.id.password_reg_input);
        email_et = findViewById(R.id.email_reg_input);
        loginTxtBtn = findViewById(R.id.loginOption);
        progressBar = findViewById(R.id.progressBar_signup);

        // set click listeners for all clickable components
        regBtn.setOnClickListener(this);
        loginTxtBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                registerUser();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.loginOption:
                startActivity(new Intent(this, LoginActivity.class));
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

        // write user object to firebase db
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // check if user has been registered
                            User user = new User(username, email); // create user from input

                            FirebaseDatabase.getInstance().getReference("Users")
                                    // return id for registered user & link it to user object
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // check user has been added to db
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Successful registration", Toast.LENGTH_LONG).show();

                                        // redirect to login
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Registration failed. Please try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(SignupActivity.this, "Registration failed. Please try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}