package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    TextView splitDisplay, programDisplay, nextWorkoutDisplay;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // check if current user has completed sign up flow:
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("program_type"))
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        // check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        } else {
            // get activity components
            splitDisplay = findViewById(R.id.userSplit);
            programDisplay = findViewById(R.id.userProgram);
            nextWorkoutDisplay = findViewById(R.id.userNextWorkout);

            getProgramData();
        }
    }

    private void getProgramData() {

        // get user program split & update UI
        ref.child("split").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String split = snapshot.getValue(String.class) + "-day split";
                splitDisplay.setText(split);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebase", error.getMessage());
            }
        });

        // get user training program type & update UI
        ref.child("program_type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String programType = snapshot.getValue(String.class).toUpperCase();
                programDisplay.setText(programType);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebase", error.getMessage());
            }
        });

        // get next workout & update UI
    }
}