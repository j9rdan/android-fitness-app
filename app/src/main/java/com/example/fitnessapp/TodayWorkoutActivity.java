package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
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

public class TodayWorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView todayDate, todayWorkoutName;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_workout);

        // initialise activity components
        todayDate = findViewById(R.id.todayDate);
        todayWorkoutName = findViewById(R.id.todayWorkoutName);
        btn_complete = findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(this);
        btn_timer = findViewById(R.id.btn_timer);
        btn_timer.setOnClickListener(this);
        recyclerView = findViewById(R.id.todayWorkoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
                }

                // get day # for previous selected workout
                int dayCount;
                for (dayCount = 0; dayCount < storedWorkouts_al.size(); dayCount++) {
                    if (storedWorkouts_al.get(dayCount).getKey().equals(today)) { dayCount++;
                        break;
                    }
                }

                // format data
                String[] workoutData = snapshot.child(today).getValue().toString().split(";");
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
                todayDate.setText("TODAY | Day " + dayCount);
                todayWorkoutName.setText(workoutData[workoutData.length-1].toUpperCase() + " DAY");
                adapter = new ExerciseListAdapter(formattedData, getApplicationContext());
                recyclerView.setAdapter(adapter);

            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view) {

    }
}