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
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView date, workoutName;
    private Button btn_done;
    private RecyclerView recyclerView;
    private RatingListAdapter adapter;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    private DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    private String today = DateHandler.getToday();

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // initialise shared prefs editor
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // initialise activity components
        date = findViewById(R.id.date);
        workoutName = findViewById(R.id.workoutName);
        btn_done = findViewById(R.id.btn_done);
        recyclerView = findViewById(R.id.workoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set views
        date.setText(pref.getString("thisWorkoutDate",""));
        workoutName.setText(pref.getString("thisWorkoutName",""));

        workoutsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = pref.getString("selectedDate", "");

                // get children & store in array list
                Iterable<DataSnapshot> storedWorkouts_i = snapshot.getChildren();
                List<DataSnapshot> storedWorkouts_al = new ArrayList<>();
                for (DataSnapshot d : storedWorkouts_i) {
                    storedWorkouts_al.add(d);
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
                adapter = new RatingListAdapter(formattedData, getApplicationContext());
                recyclerView.setAdapter(adapter);

                // adjust workout volume & update db
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] results = adjustVolume(workoutData);
                        updateDatabase(results);
                        startActivity(new Intent(EvaluationActivity.this, HomeActivity.class));
                    }
                });

            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view) {

    }

    public ArrayList<String> getRatings() {

        ArrayList<String> ratings = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        RatingListAdapter.RatingViewHolder childHolder;

        for (int i = 0; i < childCount; i++) {
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof RatingListAdapter.RatingViewHolder) {
                childHolder = (RatingListAdapter.RatingViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                if (childHolder.rating.getText().toString().isEmpty()) {
                    ratings.add(String.valueOf(0)); // default value
                } else {
                    ratings.add(childHolder.rating.getText().toString());
                }
            }
        }
        return ratings;
    }

    public String[] adjustVolume(String[] workout) {

        // get ratings
        ArrayList<String> ratings_str = getRatings();

        // numeric arrays for ratings & associated multipliers
        double[] ratings = new double[ratings_str.size()];
        double[] multipliers = new double[ratings_str.size()];
        double mult;

        for (int i = 0; i < ratings_str.size(); i++) {
            ratings[i] = Double.parseDouble(ratings_str.get(i));    // convert each str to double

            // multiplier to determine amount to adjust weight by
            if (ratings[i] <= 3) { mult = 1.5;
            } else if (ratings[i] >= 3 && ratings[i] <= 6.5) { mult = 1.25;
            } else if (ratings[i] >= 6.5 && ratings[i] <= 9) { mult = 1.1;
            } else { mult = 0.95;
            }

            multipliers[i] = mult;  // add to multipliers list
        }

        Log.w("RATINGS", Arrays.toString(ratings));
        Log.w("MULTS", Arrays.toString(multipliers));

        String completeToday = "";
        String nextWorkout = "";

        for (int i = 0; i < workout.length-1; i++) {
            String[] exercise = workout[i].split(",");
            double weight = Double.parseDouble(exercise[1]) * multipliers[i];
            double newWeight = 2.5 * (Math.round(weight/2.5)); // rounds to the nearest 2.5kg
            completeToday += exercise[0] + "," + exercise[1] + "," + ratings[i] + ";";
            nextWorkout += exercise[0] + "," + newWeight + ";";
        }
        completeToday += workoutName.getText().toString().toLowerCase().substring(0,4);
        nextWorkout += workoutName.getText().toString().toLowerCase().substring(0,4);

        Log.w("LOGGED", completeToday);
        Log.w("NEXT WEEK", nextWorkout);
        Log.w("TODAY", today);

        return new String[]{completeToday, nextWorkout};

    }

    public void updateDatabase(String[] results) {
        String completeToday = results[0];
        String nextWorkout = results[1];

        // overwrite current child with ratings & create a new workout for same day next week
        workoutsRef.child(today).setValue(completeToday);
        userRef.child("split").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String futureDate;
                int step = 0;
                int split = Integer.parseInt(snapshot.getValue().toString());
                switch (split) {
                    case 3: step = 7;break;
                    case 4: step = 4;break;
                    case 5: step = 3;break;
                    case 6: step = 2;break;
                }
                futureDate = DateHandler.getFutureDate(step);
                Log.w("FUTUREDATE", futureDate);
                workoutsRef.child(futureDate).setValue(nextWorkout);
                Toast.makeText(EvaluationActivity.this, futureDate + ": Workout created", Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });


    }
}