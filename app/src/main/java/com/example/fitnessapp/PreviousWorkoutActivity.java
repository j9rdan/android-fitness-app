package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.List;

public class PreviousWorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView prevWorkoutDate, prevWorkoutName;
    private Button btn_close, btn_share;
    private RecyclerView recyclerView;
    private ExerciseListAdapter adapter;
    private ShareActionProvider mySAP;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_workout);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        prevWorkoutDate = findViewById(R.id.prevWorkoutDate);
        prevWorkoutName = findViewById(R.id.prevWorkoutName);
        btn_share = findViewById(R.id.btn_sharePrevWorkout);
        btn_share.setOnClickListener(this);
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
                    if (storedWorkouts_al.get(dayCount).getKey().equals(date)) { dayCount++; break; }
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
                prevWorkoutDate.setText(date + " | Day " + (dayCount-1));
                prevWorkoutName.setText(workoutData[workoutData.length-1].toUpperCase() + " DAY");
                adapter = new ExerciseListAdapter(formattedData, getApplicationContext());
                recyclerView.setAdapter(adapter);
                editor.clear();


            }
            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.share_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.btn_sharePrevWorkout);
//        mySAP = (ShareActionProvider) menuItem.getActionProvider();
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.btn_sharePrevWorkout) {
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onClick(View view) {

//        if (view.getId() == R.id.btn_sharePrevWorkout) {
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/html");
//            intent.putExtra(Intent.EXTRA_TEXT, "Share this workout");
//            mySAP.setShareIntent(intent);
//        }

    }
}