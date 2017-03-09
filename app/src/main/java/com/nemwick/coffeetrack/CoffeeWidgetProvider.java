package com.nemwick.coffeetrack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;


public class CoffeeWidgetProvider extends AppWidgetProvider {

    //called when Widget is added to Host and when widget button is pressed
    //no automatic periodic update
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName me = new ComponentName(context, CoffeeWidgetProvider.class);
        appWidgetManager.updateAppWidget(me, buildUpdate(context, appWidgetIds));

    }

    private RemoteViews buildUpdate(Context context, int[] appWidgetIds) {
        //set up the widget
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_coffee);

        Intent intent = new Intent(context, CoffeeWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        //TODO:  save coffee consumed time to ContentProvider/db & SavedPreferences
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd - HH:mm");
        String currentDateTime = sdf.format(new Date());
        updateViews.setTextViewText(R.id.date_time_last_coffee, currentDateTime);

        updateViews.setOnClickPendingIntent(R.id.widget_coffee_button, pendingIntent);

        return updateViews;
    }

}
