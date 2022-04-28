package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buildMuscle, buildStrength, buildEndurance, loseFat, homeWorkouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_program);

        // get buttons & set click listeners

        buildMuscle = findViewById(R.id.btn_muscle);
        buildMuscle.setOnClickListener(this);

        buildStrength = findViewById(R.id.btn_strength);
        buildStrength.setOnClickListener(this);

        buildEndurance = findViewById(R.id.btn_endurance);
        buildEndurance.setOnClickListener(this);

        loseFat = findViewById(R.id.btn_fat);
        loseFat.setOnClickListener(this);

        homeWorkouts = findViewById(R.id.btn_homeWorkouts);
        homeWorkouts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_muscle:
                // set user program type to hypertrophy
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
            case R.id.btn_strength:
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
            case R.id.btn_endurance:
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
            case R.id.btn_fat:
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
            case R.id.btn_homeWorkouts:
                startActivity(new Intent(SelectProgramActivity.this, AboutYouActivity.class));
                break;
        }

    }
}