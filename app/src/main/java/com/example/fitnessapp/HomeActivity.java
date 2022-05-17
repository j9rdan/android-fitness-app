package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView splitDisplay, programDisplay, nextWorkoutDisplay;
    private Button btn_start;
    private android.widget.CalendarView calendarHome;
    private BottomNavigationView bottomNav;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    // get current date
    private String today = DateHandler.getToday();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // define shared preferences
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        editor.putString("today", today);

        // get activity components
        splitDisplay = findViewById(R.id.userSplit);
        programDisplay = findViewById(R.id.userProgram);
        nextWorkoutDisplay = findViewById(R.id.userNextWorkout);
        btn_start = findViewById(R.id.btn_startWorkout);
        btn_start.setOnClickListener(this);
        calendarHome = findViewById(R.id.calendarView);
        calendarHome.setOnClickListener(this);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.home);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {  // set up navigation
                    case R.id.home:
                        return true;
                    case R.id.progress:
                        startActivity(new Intent(getApplicationContext(), AnalyticsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(), MoreInfoActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

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
                // format date
                String dateSelection = DateHandler.formatDate(year + "-" + (month+1) + "-" + dayOfMonth);

                workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // if there is no saved workout for a past date
                        if (!snapshot.hasChild(dateSelection) && dateSelection.compareTo(today) < 0) {
                            Toast.makeText(HomeActivity.this, dateSelection + ": No recorded workout", Toast.LENGTH_SHORT).show();
                            Log.w("COMPARETO", String.valueOf(dateSelection.compareTo(today)));
                            // if there is no workout for a future date
                        } else if (!snapshot.hasChild(dateSelection) && dateSelection.compareTo(today) > 0) {
                            Toast.makeText(HomeActivity.this, dateSelection + ": No upcoming workout", Toast.LENGTH_SHORT).show();
                            Log.w("COMPARETO", String.valueOf(dateSelection.compareTo(today)));
                            // if chosen date has past workout
                        } else if (dateSelection.compareTo(today) < 0) {
                            editor.putString("selectedDate", dateSelection);
                            editor.apply();
                            startActivity(new Intent(HomeActivity.this, PreviousWorkoutActivity.class));
                            // if user clicks today/future date
                        } else if (dateSelection.compareTo(today) >= 0) {
                            editor.putString("selectedDate", dateSelection);
                            editor.apply();
                            startActivity(new Intent(HomeActivity.this, NextWorkoutActivity.class));
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
                String split = "";
                if (!snapshot.getValue(String.class).equals(""))
                    split = snapshot.getValue(String.class) + "-day split";
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
                String programType = "";
                if (!snapshot.getValue(String.class).equals(""))
                    programType = snapshot.getValue(String.class).toUpperCase();
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

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
                }
                // get position of next workout & day # for selected workout
                int i;
                for (i = 0; i < storedWorkouts_al.size(); i++) {
                    if (storedWorkouts_al.get(i).getKey().equals(today)) break;
                }

                Log.w("LAST WORKOUT", storedWorkouts_al.get((storedWorkouts_al.size()-1)).getKey());


                if (storedWorkouts_al.get((storedWorkouts_al.size()-1)).getKey().compareTo(today) > 0) {
                    // if there is an upcoming workout
                    String[] nextWorkout = storedWorkouts_al.get(i+1).getValue().toString().split(";");
                    String nextWorkoutName = nextWorkout[nextWorkout.length-1];
                    Log.i("CHILDREN", storedWorkouts_al.toString());
                    Log.i("NEXT", nextWorkoutName);
                    nextWorkoutDisplay.setText(nextWorkoutName.toUpperCase());
                } else {
                    nextWorkoutDisplay.setText("NOTHING");
                }

            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_startWorkout:
                editor.putString("selectedDate", today);
                editor.apply();
                startActivity(new Intent(HomeActivity.this, NextWorkoutActivity.class));
                break;
            case R.id.btn_timer:
                break;
        }
    }

}