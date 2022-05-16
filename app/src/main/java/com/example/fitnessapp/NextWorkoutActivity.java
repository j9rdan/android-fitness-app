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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class NextWorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nextDate, nextWorkoutName;
    private Button btn_complete, btn_timer;
    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    // get current date
    Calendar c = Calendar.getInstance();
    int dayNow = c.get(Calendar.DAY_OF_MONTH);
    int monthNow = c.get(Calendar.MONTH)+1;
    int yearNow = c.get(Calendar.YEAR);
    String today = dayNow + "-" + monthNow + "-" + yearNow;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_workout);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // initialise activity components
        nextDate = findViewById(R.id.nextDate);
        nextWorkoutName = findViewById(R.id.nextWorkoutName);
        btn_complete = findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(this);
        btn_timer = findViewById(R.id.btn_timer);
        btn_timer.setOnClickListener(this);
        recyclerView = findViewById(R.id.nextWorkoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String date = pref.getString("selectedDate", "");

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
                }

                // get day # for selected workout
                int dayCount;
                for (dayCount = 0; dayCount < storedWorkouts_al.size(); dayCount++) {
                    if (storedWorkouts_al.get(dayCount).getKey().equals(date)) { dayCount++;
                        break;
                    }
                }

                // format data
                String[] workoutData = snapshot.child(date).getValue().toString().split(";");
                Log.w("READ", Arrays.toString(workoutData));
                ArrayList<String> workoutData_al = new ArrayList<>();
                for (int i = 0; i < workoutData.length-1; i++) {
                    // workout_name - Xkg
                    workoutData_al.add(workoutData[i].split(",")[0] + " - "
                            + workoutData[i].split(",")[1]
                            + " kg");
                }
                String[] formattedData = workoutData_al.toArray(new String[workoutData_al.size()]);
                Log.w("FORMAT", Arrays.toString(formattedData));

                // set displays
                if (date.equals(today)) {
                    nextDate.setText("TODAY | Day " + dayCount);
                } else {
                    nextDate.setText(date + " | Day " + dayCount);
                    btn_complete.setVisibility(View.INVISIBLE); // hide action buttons for future dates
                    btn_timer.setVisibility(View.INVISIBLE);
                }
                nextWorkoutName.setText(workoutData[workoutData.length-1].toUpperCase() + " DAY");
                adapter = new ExerciseListAdapter(formattedData, getApplicationContext());
                recyclerView.setAdapter(adapter);


            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_complete:
                startActivity(new Intent(NextWorkoutActivity.this, EvaluationActivity.class));
                break;

            case R.id.btn_timer:
                // open alarm clock or set timer for current time +2mins
                break;
        }


    }
}