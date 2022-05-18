package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNav;
    private TextView usernameTxtView;
    private Button btn_editProfile, btn_logOut;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String username;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        // initialise shared prefs editor
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        username = pref.getString("currentUsername","");

        // initialise activity components
        usernameTxtView = findViewById(R.id.usernameTxtView);
        usernameTxtView.setText(username);
        btn_editProfile = findViewById(R.id.btn_editProfile);
        btn_editProfile.setOnClickListener(this);
        btn_logOut = findViewById(R.id.btn_logOut);
        btn_logOut.setOnClickListener(this);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.profile);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // set up navigation
                switch(item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.insights:
                        startActivity(new Intent(getApplicationContext(), AnalyticsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.help:
                        startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_editProfile:
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                break;

            case R.id.btn_logOut:
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

        }

    }
}