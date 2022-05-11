package com.example.fitnessapp;

import java.util.Map;

public class User {

    public String username, email;

    public User() {

        // empty constructor allowing access to class variables for empty objects
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
