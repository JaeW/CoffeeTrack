package com.nemwick.coffeetrack;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;


public class CoffeeWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName me = new ComponentName(context, CoffeeWidgetProvider.class);
        appWidgetManager.updateAppWidget(me, buildUpdate(context, appWidgetIds));

    }

    private RemoteViews buildUpdate(Context context, int[] appWidgetIds) {

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_coffee);
        //Intent i = new Intent(context, CoffeeWidgetProvider.class);



        return updateViews;
    }

}
