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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreviousWorkoutActivity extends AppCompatActivity {

    private TextView prevWorkoutDate, prevWorkoutName;
    private Button btn_close, btn_share;

    // recycler view
    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_workout);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        Log.w("TODAY", pref.getString("today",""));

        prevWorkoutDate = findViewById(R.id.prevWorkoutDate);
        prevWorkoutName = findViewById(R.id.prevWorkoutName);
        btn_share = findViewById(R.id.btn_sharePrevWorkout);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // share action provider
            }
        });
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


        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
                }

                String date = pref.getString("selectedDate", "");
                Log.w("DATE", date);

                // get day # for previous selected workout
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
                    // workout_name - Xkg   Y/10
                    workoutData_al.add(workoutData[i].split(",")[0] + " - "
                            + workoutData[i].split(",")[1]
                            + " kg\t\t\t" + workoutData[i].split(",")[2] + "/10");
                }
                String[] formattedData = workoutData_al.toArray(new String[workoutData_al.size()]);
                Log.w("FORMAT", Arrays.toString(formattedData));

                // set displays
                prevWorkoutDate.setText(date + " | Day " + dayCount);
                prevWorkoutName.setText(workoutData[workoutData.length-1].toUpperCase() + " DAY");
                adapter = new ExerciseListAdapter(formattedData, getApplicationContext());
                recyclerView.setAdapter(adapter);
                editor.clear();


            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}