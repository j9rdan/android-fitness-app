package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView splitDisplay, programDisplay, nextWorkoutDisplay;
    Button btn_start;
    android.widget.CalendarView calendarHome;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get activity components
        splitDisplay = findViewById(R.id.userSplit);
        programDisplay = findViewById(R.id.userProgram);
        nextWorkoutDisplay = findViewById(R.id.userNextWorkout);

        btn_start = findViewById(R.id.btn_startWorkout);
        btn_start.setOnClickListener(this);

        calendarHome = findViewById(R.id.calendarView);
        calendarHome.setOnClickListener(this);

        // check if current user has completed sign up flow:
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
            getProgramData();
        }

        calendarHome.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;

                workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild(date)) { // if there is no saved workout for a date
                            Toast.makeText(HomeActivity.this, date + ": No recorded workout", Toast.LENGTH_SHORT).show();
                        } else {
                            // open workout info
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

    }

    private void getProgramData() {

        // get user program split & update UI
        userRef.child("split").addValueEventListener(new ValueEventListener() {
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
        userRef.child("program_type").addValueEventListener(new ValueEventListener() {
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

        // get next workout
        workoutsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get current date
                Calendar c = Calendar.getInstance();
                int dayNow = c.get(Calendar.DAY_OF_MONTH);
                int monthNow = c.get(Calendar.MONTH)+1;
                int yearNow = c.get(Calendar.YEAR);
                String today = dayNow + "-" + monthNow + "-" + yearNow;

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
                }
                // get position of next workout
                int i;
                for (i = 0; i < storedWorkouts_al.size(); i++) {
                    if (storedWorkouts_al.get(i).getKey().equals(today)) break;
                }
                String[] nextWorkout = storedWorkouts_al.get(i+1).getValue().toString().split(";");
                String nextWorkoutName = nextWorkout[nextWorkout.length-1];

                Log.i("CHILDREN", storedWorkouts_al.toString());
                Log.i("NEXT", nextWorkoutName);

                nextWorkoutDisplay.setText(nextWorkoutName.toUpperCase());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view) {

    }
}