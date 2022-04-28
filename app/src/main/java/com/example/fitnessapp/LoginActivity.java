package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    private String email;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        username_et = findViewById(R.id.username_login_input);
        password_et = findViewById(R.id.password_login_input);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgotPWOption);
        signUpOption = findViewById(R.id.regOption);
        progressBar = findViewById(R.id.progressBar_login);

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
                startActivity(new Intent(this, SignupActivity.class));
        }

    }

    private void resetPassword() {
    }

    private void signUserIn() {

        // TO DO: STORE RETRIEVED EMAIL IN GLOBAL STRING

        // store inputs as strings
        String username = username_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        String[] email = new String[1];

        // get associated email from username
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = rootRef.orderByChild("username").equalTo(username);
        query.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        email[0] = ds.child("email").getValue(String.class);
                        String s = "Queried email 1: " + email[0];
                        Log.i(null, s);
                    }
                } else {
                    Log.i("TAG", task.getException().getMessage()); //Don't ignore potential errors!
                }
            }
        });

//        rootRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if (dataSnapshot.child("username").getValue().toString().equals(username))
//                        email = dataSnapshot.child("email").getValue().toString();
//                }
//                String s = "Queried email 1: " + email;
//                Log.i(null, s);
////                email[0] = email_db;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

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

        progressBar.setVisibility(View.VISIBLE);
        String s = "Queried email 2: " + email[0];
        Log.i(null, s);
        String newEmail = email[0];
        String s2 = "new email: " + newEmail;
        Log.i(null, s2);
//        mAuth.signInWithEmailAndPassword(email[0], password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    startActivity(new Intent(LoginActivity.this, SelectProgramActivity.class));
//                    // redirect
//                } else {
//                    Toast.makeText(LoginActivity.this, "Wrong credentials. Please try again", Toast.LENGTH_LONG).show();
//                }
//            }
//        });



    }

    public String getUserEmail(String user) {
        return user;
    }
}