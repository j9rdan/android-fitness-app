package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PreviousWorkoutActivity extends AppCompatActivity {

    private TextView prevWorkoutDate, prevWorkoutName;
    private Button btn_close;

    // recycler view
    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;
    final private String[] testStr = {"a", "b", "c", "d", "e", "f"};

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_workout);

        prevWorkoutDate = findViewById(R.id.prevWorkoutDate);
        prevWorkoutName = findViewById(R.id.prevWorkoutName);
        btn_close = findViewById(R.id.btn_closePrevWorkout);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PreviousWorkoutActivity.this, HomeActivity.class));
            }
        });

        // recycler view
        recyclerView = findViewById(R.id.prevWorkoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseListAdapter(testStr, this);
        recyclerView.setAdapter(adapter);

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                String date = pref.getString("selectedDate", "");
                int dayCount = pref.getInt("dayCount",0);
                String[] workoutData = snapshot.child(date).getValue().toString().split(";");
                prevWorkoutDate.setText(date + " | Day " + dayCount);
                prevWorkoutName.setText(workoutData[workoutData.length-1].toUpperCase() + " DAY");


            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}