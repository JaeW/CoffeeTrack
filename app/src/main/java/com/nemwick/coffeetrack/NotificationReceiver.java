package com.nemwick.coffeetrack;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class NotificationReceiver extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "com.newick.coffeetrack.notification-id";
    public static String NOTIFICATION = "com.nemwick.coffeetrack.notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

        SharedPreferences preferences =
                context.getSharedPreferences(MainActivity.COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainActivity.SESSION_TIMER_STATE, false);
        editor.apply();

    }
}
