package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChooseSplitActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_3days, btn_4days, btn_5days, btn_6days;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("Users").child(mAuth.getCurrentUser().getUid());
    private String chosenSplit;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_split);

        // define shared preferences
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // get buttons & set click listeners
        btn_3days = findViewById(R.id.btn_3days);
        btn_4days = findViewById(R.id.btn_4days);
        btn_5days = findViewById(R.id.btn_5days);
        btn_6days = findViewById(R.id.btn_6days);
        btn_3days.setOnClickListener(this);
        btn_4days.setOnClickListener(this);
        btn_5days.setOnClickListener(this);
        btn_6days.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // store workout split for current user
            case R.id.btn_3days:
                userRef.child("split").setValue("3");
                chosenSplit = "3";
                break;
            case R.id.btn_4days:
                userRef.child("split").setValue("4");
                chosenSplit = "4";
                break;
            case R.id.btn_5days:
                userRef.child("split").setValue("5");
                chosenSplit = "5";
                break;
            case R.id.btn_6days:
                userRef.child("split").setValue("6");
                chosenSplit = "6";
                break;
        }

        getProgram();
    }

    private void getProgram() {

        ArrayList<String> programDays = new ArrayList<>();

        String programType = pref.getString("chosenProgram", "");
        String targetMuscle = pref.getString("targetedMuscle",""); // "" if strength
        String split = chosenSplit;

        Query programsRef = database.getReference("Programs").
                child(programType).
                    child(targetMuscle);

        DatabaseReference workoutsRef = database.getReference("Workouts").child(mAuth.getCurrentUser().getUid());

        Log.w("PROGRAMTYPE", programType);
        Log.w("TARGET", targetMuscle);

        programsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot d : task.getResult().getChildren()) {
                        programDays.add(d.getValue((String.class)));
                    }

                    ArrayList<String> dates = DateHandler.generateDates(Integer.parseInt(split));
                    Log.w("DATES", dates.toString());

                    int i = 2;  // 2 = push day, 1 = pull day, 0 = legs
                    for (String date : dates) {
                        workoutsRef.child(date).setValue(programDays.get(i));
                        Toast.makeText(ChooseSplitActivity.this, "Upcoming workouts:\n" + date, Toast.LENGTH_SHORT).show();
                        i--;
                        if (i < 0) i = 2;
                    }
                    workoutsRef.child(DateHandler.getFutureDate(-1)).setValue("N/A,0,none ;program creation");
                    load();
                }
            }
        });



    }

    public void load() {
        startActivity(new Intent(ChooseSplitActivity.this, LoadingActivity.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ChooseSplitActivity.this, HomeActivity.class));
            }
        }, 2000);
    }
}