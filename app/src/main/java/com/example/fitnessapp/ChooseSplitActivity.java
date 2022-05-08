package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseSplitActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_3days, btn_4days, btn_5days, btn_6days;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_split);

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
                ref.child("split").setValue("3");
                break;
            case R.id.btn_4days:
                ref.child("split").setValue("4");
                break;
            case R.id.btn_5days:
                ref.child("split").setValue("5");
                break;
            case R.id.btn_6days:
                ref.child("split").setValue("6");
                break;
        }
        startActivity(new Intent(this, HomeActivity.class));
    }
}