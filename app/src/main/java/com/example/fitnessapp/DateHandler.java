package com.example.fitnessapp;

import java.util.Calendar;

public class DateHandler {

    public static String formatDate(String date) {

        int dd = Integer.parseInt(date.split("-")[2]);
        int mm = Integer.parseInt(date.split("-")[1]);
        int yyyy = Integer.parseInt(date.split("-")[0]);

        if (dd < 10 && mm < 10) {
            date = yyyy + "-0" + mm + "-0" + dd;
        } else if (dd < 10 && mm > 10) {
            date = yyyy + "-" + mm + "-0" + dd;
        } else if (dd >= 10 && mm < 10) {
            date = yyyy + "-0" + mm + "-" + dd;
        } else {
            date = yyyy + "-" + mm + "-" + dd;
        }
        return date;
    }

    public static String getToday() {
        Calendar c = Calendar.getInstance();
        int dayNow = c.get(Calendar.DAY_OF_MONTH);
        int monthNow = c.get(Calendar.MONTH)+1;
        int yearNow = c.get(Calendar.YEAR);
        String today = yearNow + "-" + monthNow + "-" + dayNow;

        return DateHandler.formatDate(today);
    }

    public static String getFutureDate(int daysFromNow) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, daysFromNow);
        int dd = c.get(Calendar.DAY_OF_MONTH);
        int mm = c.get(Calendar.MONTH)+1;
        int yyyy = c.get(Calendar.YEAR);
        String date = yyyy + "-" + mm + "-" + dd;
        return DateHandler.formatDate(date);
    }
}
