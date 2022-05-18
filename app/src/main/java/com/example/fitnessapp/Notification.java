package com.example.fitnessapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {//creates a notfication
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyUpdate")//sets the channelid used in reminder
                .setSmallIcon(R.drawable.ic_welift_full)
                .setContentTitle("we-lift.")
                .setContentText("Remember to drink water!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        // remind users to drink water after a workout
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

    }
}
