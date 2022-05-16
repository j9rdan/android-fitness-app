package com.example.fitnessapp;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

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

    String[] formattedData = {"a", "b", "c"};

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
        btn_done.setOnClickListener(this);
        recyclerView = findViewById(R.id.workoutRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set views
        date.setText(pref.getString("thisWorkoutDate",""));
        workoutName.setText(pref.getString("thisWorkoutName",""));

        adapter = new RatingListAdapter(formattedData, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_done) {
            Log.w("RATINGS", getRatings().toString());

        }

    }

    public ArrayList<String> getRatings() {

        ArrayList<String> ratings = new ArrayList<>();

        int childCount = recyclerView.getChildCount();
        RatingListAdapter.RatingViewHolder childHolder;
        for (int i = 0; i < childCount; i++) {
            if (recyclerView.findViewHolderForLayoutPosition(i) instanceof RatingListAdapter.RatingViewHolder) {
                childHolder = (RatingListAdapter.RatingViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                ratings.add(childHolder.rating.getText().toString());
                Log.w("EDITTEXT", childHolder.rating.getText().toString());
            }
        }
        return ratings;
    }
}