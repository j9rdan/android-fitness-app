package com.example.fitnessapp;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class SelectMusclesActivity extends AppCompatActivity implements View.OnClickListener {

    // all muscles that can be targeted

    final String[] allMuscles = {"Chest", "Shoulders", "Triceps", "Back", "Biceps", "Legs"};
    private List<String> targetedMuscles;   // user-selected muscles

    // declare views
    private Button btn_next;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutMgr;
    private TargetMusclesAdapter adapter;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_muscles);

//        editor.remove("targetedMuscle");
//        editor.apply();

        // define shared preferences
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // get views
        recyclerView = findViewById(R.id.musclesRecycler);
        btn_next = findViewById(R.id.btn_next_pickMuscles);
        btn_next.setOnClickListener(this);

        // set layout
        layoutMgr = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutMgr);
        recyclerView.setItemViewCacheSize(allMuscles.length);
        adapter = new TargetMusclesAdapter(Arrays.asList(allMuscles), getApplicationContext(), recyclerView);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {

        if (btn_next.isPressed()) {

            targetedMuscles = adapter.getSelectedValues(); // get chosen muscles

            // selection validation
            if (targetedMuscles.size() != 1) {
                Toast.makeText(SelectMusclesActivity.this, "Choose 1 muscle", Toast.LENGTH_LONG).show();
                return;
            }

            // save targeted muscles to current user
            ref.child("target_muscles").setValue(targetedMuscles.get(0));
            editor.putString("targetedMuscle", targetedMuscles.get(0));
            editor.apply();


            // switch activity
            startActivity(new Intent(this, ChooseSplitActivity.class));
        }

    }
}