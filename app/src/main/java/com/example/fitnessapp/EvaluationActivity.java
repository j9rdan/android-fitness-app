package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView date, workoutName;
    private Button btn_done;
    private RecyclerView recyclerView;
    private RatingListAdapter adapter;

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

//    String[] formattedData = {"a", "b", "c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // initialise activity components
        date = findViewById(R.id.date);
        workoutName = findViewById(R.id.workoutName);
        btn_done = findViewById(R.id.btn_done);
//        btn_done.setOnClickListener(this);
        recyclerView = findViewById(R.id.workoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set views
        date.setText(pref.getString("thisWorkoutDate",""));
        workoutName.setText(pref.getString("thisWorkoutName",""));

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

                // adjust volume
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adjustVolume(workoutData, getRatings());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {

//        if (view.getId() == R.id.btn_done) {
//            Log.w("RATINGS", getRatings().toString());
////            adjustVolume(getRatings());
//
//        }

    }

    public ArrayList<String> getRatings() {

        ArrayList<String> ratings = new ArrayList<>();

        int childCount = recyclerView.getChildCount();
        RatingListAdapter.RatingViewHolder childHolder;
        for (int i = 0; i < childCount; i++) {
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof RatingListAdapter.RatingViewHolder) {
                childHolder = (RatingListAdapter.RatingViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                if (childHolder.rating.getText().toString().isEmpty()) {
                    ratings.add(String.valueOf(0));
                } else {
                    ratings.add(childHolder.rating.getText().toString());
                }

                Log.w("EDITTEXT", childHolder.rating.getText().toString());
            }
        }
        return ratings;
    }

    public void adjustVolume(String[] workout, ArrayList<String> ratings_str) {

        // get ratings
        ratings_str = getRatings();

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
        String completeToday = "";
        String nextWorkout = "";

        for (int i = 0; i < workout.length-1; i++) {
            String[] exercise = workout[i].split(",");
//            Log.w("EXERCISE", Arrays.toString(exercise));
//            Log.w("EXERCISE[i]", exercise[0]);
            double weight = Double.parseDouble(exercise[1]) * multipliers[i];
            double newWeight = 2.5 * (Math.round(weight/2.5)); // rounds to the nearest 2.5kg
            Log.w("NEW_WEIGHT", String.valueOf(newWeight));
            completeToday += exercise[0] + "," + exercise[1] + "," + ratings[i] + ";";
            nextWorkout += exercise[0] + "," + String.valueOf(newWeight) + ";";

        }
        completeToday += workoutName.getText().toString().toLowerCase().substring(0,4);
        nextWorkout += workoutName.getText().toString().toLowerCase().substring(0,4);

        Log.w("LOGGED", completeToday);
        Log.w("NEXT WEEK", nextWorkout);
//        Log.w("RATINGS", Arrays.toString(ratings));
//        Log.w("MULTS", Arrays.toString(multipliers));


    }
}