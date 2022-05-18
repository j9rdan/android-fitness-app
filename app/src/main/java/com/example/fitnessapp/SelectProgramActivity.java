package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buildMuscle, buildStrength;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String chosenProgram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_program);

        // define shared preferences
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        // get buttons & set click listeners

        buildMuscle = findViewById(R.id.btn_muscle);
        buildMuscle.setOnClickListener(this);

        buildStrength = findViewById(R.id.btn_strength);
        buildStrength.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_muscle:
                chosenProgram = "Hypertrophy";  // set user program type to hypertrophy (muscle growth)
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
            case R.id.btn_strength:
                chosenProgram = "Strength";
                editor.putString("targetedMuscle", "General");
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
        }
        ref.child("program_type").setValue(chosenProgram);
        editor.putString("chosenProgram", chosenProgram);
        editor.apply();

    }
}