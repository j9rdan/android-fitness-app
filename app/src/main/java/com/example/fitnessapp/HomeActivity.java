package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // get activity components
        splitDisplay = findViewById(R.id.userSplit);
        programDisplay = findViewById(R.id.userProgram);
        nextWorkoutDisplay = findViewById(R.id.userNextWorkout);

        // get user program split & update UI
        ref.child("split").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String split;
                if (task.isSuccessful()) {
                    split = String.valueOf(task.getResult().getValue()) + "-day split";
                    splitDisplay.setText(split);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        // get user training program type & update UI
        ref.child("program_type").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String programType;
                if (task.isSuccessful()) {
                    programType = String.valueOf(task.getResult().getValue()).toUpperCase();
                    programDisplay.setText(programType);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

    }
}