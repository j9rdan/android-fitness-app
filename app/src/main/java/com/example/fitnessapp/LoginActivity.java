package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username_et, password_et;
    private Button loginBtn;
    private TextView forgotPassword, signUpOption;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance(); // get firebase authentication instance

        // initialise shared prefs editor
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // initialise all activity components
        username_et = findViewById(R.id.username_login_input);
        password_et = findViewById(R.id.password_login_input);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPWOption);
        signUpOption = findViewById(R.id.regOption);
        progressBar = findViewById(R.id.progressBar_login);

        // set click listeners for all clickable components
        loginBtn.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUpOption.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginBtn:
                signUserIn();
                username_et.setText("");
                password_et.setText("");
                break;

            case R.id.regOption:
                startActivity(new Intent(this, SignupActivity.class));
        }
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
        } else if (password.isEmpty()) {
            password_et.setError("Enter a password");
            password_et.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // query Users data for child node with input username
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = rootRef.orderByChild("username").equalTo(username);
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String email = null;

                if (task.isSuccessful()) {
                    // get associated email from username
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        email = ds.child("email").getValue(String.class);
                    }

                    if (email != null) { // if user exists
                        // sign in with given credentials
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editor.putString("currentUsername", username);
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Wrong credentials. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "User not found. Register an account", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                    }

                }
            }
        });
    }
}