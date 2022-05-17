package com.example.fitnessapp;

import java.util.Calendar;

public class DateHandler {

    public static String formatDate(String date) {

        int dd = Integer.parseInt(date.split("-")[0]);
        int mm = Integer.parseInt(date.split("-")[1]);
        int yyyy = Integer.parseInt(date.split("-")[2]);

        if (dd < 10 && mm < 10) {
            date = "0" + dd + "-0" + mm + "-" + yyyy;
        } else if (dd < 10 && mm > 10) {
            date = "0" + dd + "-" + mm + "-" + yyyy;
        } else if (dd > 10 && mm < 10) {
            date = dd + "-0" + mm + "-" + yyyy;
        } else {
            date = dd + "-" + mm + "-" + yyyy;
        }
        return date;
    }

    public static String getToday() {
        Calendar c = Calendar.getInstance();
        int dayNow = c.get(Calendar.DAY_OF_MONTH);
        int monthNow = c.get(Calendar.MONTH)+1;
        int yearNow = c.get(Calendar.YEAR);
        String today = dayNow + "-" + monthNow + "-" + yearNow;

        return DateHandler.formatDate(today);
    }

    public static String get7DaysFromNow() {
        return "";
    }
}
