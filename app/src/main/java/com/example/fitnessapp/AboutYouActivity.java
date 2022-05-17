package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutYouActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_sex, btn_next;
    private EditText birthdate_et, height_et, weight_et;

    // get instances of firebase auth & realtime db
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Users").child(mAuth.getCurrentUser().getUid());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_you);

        // initialise activity components & set click listeners

        btn_sex = findViewById(R.id.sex_toggle);
        btn_sex.setOnClickListener(this);

        btn_next = findViewById(R.id.btn_next_aboutYou);
        btn_next.setOnClickListener(this);

        birthdate_et = findViewById(R.id.birthdate_field);
        height_et = findViewById(R.id.height_field);
        weight_et = findViewById(R.id.weight_field);

    }

    @Override
    public void onClick(View view) {

        String birthdate_str = birthdate_et.getText().toString().trim();
        String height_str = height_et.getText().toString().trim();
        String weight_str = weight_et.getText().toString().trim();

        if (btn_next.isPressed()) {

            // input validation
            if (birthdate_str.isEmpty()) {
                birthdate_et.setError("Enter a date of birth");
                birthdate_et.requestFocus();
                return;
            } else if (height_str.isEmpty()) {
                height_et.setError("Enter a height");
                height_et.requestFocus();
                return;
            } else if (weight_str.isEmpty()) {
                weight_et.setError("Enter a weight");
                weight_et.requestFocus();
                return;
            }

            // save user details
            if (btn_sex.getText().toString().equals("Male")) {
                ref.child("sex").setValue("male");
            } else {
                ref.child("sex").setValue("female");
            }
            ref.child("birth_date").setValue(birthdate_str);
            ref.child("height").setValue(height_str);
            ref.child("weight").setValue(weight_str);

//            startActivity(new Intent(this, TargetMusclesActivity.class));

            ref.child("program_type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(String.class).equals("Hypertrophy")) {
                        // only users who want to build muscle can target a muscle group
                        startActivity(new Intent(AboutYouActivity.this, TargetMusclesActivity.class));
                    } else {
                        startActivity(new Intent(AboutYouActivity.this, ChooseSplitActivity.class));
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

    }
}