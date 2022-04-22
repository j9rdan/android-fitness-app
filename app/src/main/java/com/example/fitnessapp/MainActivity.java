package com.example.fitnessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button regBtn;
    private TextView loginTxtBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regBtn = findViewById(R.id.button);
        regBtn.setOnClickListener(this);
        loginTxtBtn = findViewById(R.id.loginOption);
        loginTxtBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(this, SelectProgram.class));
                break;
            case R.id.loginOption:
                startActivity(new Intent(this, UserLogin.class));
                break;
        }
    }
}