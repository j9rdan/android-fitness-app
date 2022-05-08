package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TargetMusclesActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_yes, btn_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_muscles);

        // get buttons & set click listeners
        btn_yes = findViewById(R.id.btn_yesTarget);
        btn_no = findViewById(R.id.btn_noTarget);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_yesTarget:
                startActivity(new Intent(this, SelectMusclesActivity.class));
                break;
            case R.id.btn_noTarget:
                startActivity(new Intent(this, ChooseSplitActivity.class));
                break;
        }

    }
}