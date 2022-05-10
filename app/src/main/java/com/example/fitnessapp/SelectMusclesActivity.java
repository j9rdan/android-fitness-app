package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class SelectMusclesActivity extends AppCompatActivity implements View.OnClickListener {

    // TO FIX: recycler repeating selections in list --> wrong list being saved

    // all muscles that can be targeted
    final String[] allMuscles = new String[] {"Chest: upper", "Chest: mid", "Chest: lower",
                                            "Delts: anterior (front)", "Delts: lateral (side)",
                                            "Delts: posterior (rear)", "Triceps: long head",
                                            "Triceps: lateral head", "Triceps: medial head",
                                            "Back: trapezius (traps)", "Back: Latissimus dorsi (lats)",
                                            "Back: lower", "Biceps: short head",
                                            "Biceps: long head", "Biceps: brachialis",
                                            "Biceps: brachioradialis (forearm)", "Legs: quadriceps (quads)",
                                            "Legs: glutes", "Legs: hamstrings", "Legs: calves", "Core"};
    List<String> targetedMuscles;

    private Button btn_next;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutMgr;
    private TargetMusclesAdapter adapter;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_muscles);

        recyclerView = findViewById(R.id.musclesRecycler);

        btn_next = findViewById(R.id.btn_next_pickMuscles);
        btn_next.setOnClickListener(this);

        layoutMgr = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutMgr);
        adapter = new TargetMusclesAdapter(Arrays.asList(allMuscles), getApplicationContext(), recyclerView);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btn_next_pickMuscles:
                saveMusclesToTarget();
                break;
        }

    }

    private void saveMusclesToTarget() {

        targetedMuscles = adapter.getSelectedValues(); // get chosen muscles

        // selection validation
        if (targetedMuscles.size() < 1 || targetedMuscles.size() > 3) {
            Toast.makeText(SelectMusclesActivity.this, "Choose up to 3 muscles to target", Toast.LENGTH_LONG).show();
            return;
        }

        // push array to firebase
        ref.child("target_muscles").setValue(targetedMuscles);

        // switch activity
        startActivity(new Intent(this, ChooseSplitActivity.class));
    }
}