package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private TextView startDate, workoutCount, lastTarget;
    private BottomNavigationView bottomNav;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        startDate = findViewById(R.id.startDateTxtView);
        workoutCount = findViewById(R.id.workoutCount);
        lastTarget = findViewById(R.id.lastMuscleTarget);

        // get target muscle
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("target_muscles")) {
                    lastTarget.setText(snapshot.child("target_muscles").getValue(String.class));
                } else {
                    lastTarget.setText("-");
                }
                // get workout counter
                if (snapshot.hasChild("total_workouts")) {
                    workoutCount.setText(String.valueOf(snapshot.child("total_workouts").getValue(Integer.class)));
                } else {
                    workoutCount.setText("0");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        // get start date
        workoutsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() >= 2) {
                    // get children & store in array list
                    Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                    List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                    for (DataSnapshot d : storedWorkouts_i) {
                        storedWorkouts_al.add(d);
                    }
                    startDate.setText(storedWorkouts_al.get(1).getKey());
                } else {
                    startDate.setText("-");
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.insights);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // set up navigation
                switch(item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.insights:
                        return true;
                    case R.id.help:
                        startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
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
    }
}