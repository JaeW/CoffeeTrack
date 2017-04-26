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
        //configure and launch notification to be shown when broadcast receiver receives alarm event
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

        //update value in preferences of timer on/off which will trigger update in ActivityMain UI
        SharedPreferences preferences =
                context.getSharedPreferences(ActivityMain.COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ActivityMain.SESSION_TIMER_STATE, false);
        editor.apply();
    }
}
