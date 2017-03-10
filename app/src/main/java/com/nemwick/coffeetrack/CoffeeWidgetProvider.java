package com.nemwick.coffeetrack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.nemwick.coffeetrack.data.CoffeeContract;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CoffeeWidgetProvider extends AppWidgetProvider {

    public static final String COFFEE_PREFERENCES = "preferenceFile"; //key to retrieve saved preferences file
    public static final String LAST_SAVED = "lastCoffeeSaved";  //key to save/retrieve most recent uri to saved preferences
    public static final String LAST_SAVED_TIME = "lastSavedTime"; //key to save/retrieve time last coffee consumed
    public static final String EXTRA_BUTTON_PRESSED =
            "com.nemwick.coffeetrack.widget.button.was.pressed";  //key to save extra to broadcast intent
    public static final String TAG = "CoffeeWidgetProvider"; //debug log tag
    private long coffeeTime;

    //called when Widget is added to Host and when widget button is pressed
    //no automatic periodic update
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences pref = context.getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        coffeeTime = pref.getLong(LAST_SAVED_TIME, 1234);

        ComponentName me = new ComponentName(context, CoffeeWidgetProvider.class);
        appWidgetManager.updateAppWidget(me, buildUpdate(context, appWidgetIds));
    }

    private RemoteViews buildUpdate(Context context, int[] appWidgetIds) {
        //set up the widget
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_coffee);

        //build intent / pending intent which will act as Widget button click event handler
        Intent intent = new Intent(context, CoffeeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds); //adding appWidgetIds to extras required or onUpdate() will not be called
        intent.putExtra(EXTRA_BUTTON_PRESSED, true);
        //FLAG_UPDATE_CURRENT ensures extras values are updated each time onUpdate() is called
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //last time coffee consumed is saved in shared preferences; if value empty no time is displayed in widget
        if (coffeeTime != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm EEE");
            Date d = new Date(coffeeTime);
            updateViews.setTextViewText(R.id.date_time_last_coffee, sdf.format(d));
        }

        //register event handler for widget button
        updateViews.setOnClickPendingIntent(R.id.widget_coffee_button, pendingIntent);

        return updateViews;
    }

    private void addNewCoffee(Context context) {
        Log.d(TAG, "addNewCoffee: started");
        coffeeTime = java.lang.System.currentTimeMillis();

        //Save time coffee was consumed to ContentProvider/db
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LATITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LONGITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, coffeeTime);
        Uri lastAddedCoffeeUri = context.getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);

        //Save last added uri to SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_SAVED, lastAddedCoffeeUri.toString());
        editor.apply();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive started");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(TAG, "onReceive: extras != null");
            Boolean isTrue = extras.getBoolean(EXTRA_BUTTON_PRESSED);
            Log.d(TAG, "onReceive: extra value: " + isTrue);
            if (extras.getBoolean(EXTRA_BUTTON_PRESSED)) {
                Log.d(TAG, "onReceive: extra button pressed - add new coffee");
                addNewCoffee(context);
            }
        }
        Log.d(TAG, "super.onReceive: ");
        super.onReceive(context, intent); //super.onReceive() will immediately call the onUpdate() method
    }
}
