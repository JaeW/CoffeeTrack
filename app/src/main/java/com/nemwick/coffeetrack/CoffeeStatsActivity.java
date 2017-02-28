package com.nemwick.coffeetrack;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nemwick.coffeetrack.data.CoffeeContract;

import java.util.Calendar;

public class CoffeeStatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView dayCupsCoffee;
    private TextView weekCupsCoffee;
    private TextView monthCupsCoffee;
    public static final int LOADER_ID = 222;
    public static final int DAY = 1;
    public static final int WEEK = 7;
    public static final int MONTH = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_stats);

        //toolbar setup
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_statistics));

        //field - view binding
        dayCupsCoffee = (TextView) findViewById(R.id.cups_today);
        weekCupsCoffee = (TextView) findViewById(R.id.cups_week);
        monthCupsCoffee = (TextView) findViewById(R.id.cups_month);

        //cursorloader initialization
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long oneMonthAgo = this.calculatePriorDateInMillis(MONTH);
        Uri queryUri = CoffeeContract.CoffeeEntry.CONTENT_URI;
        String[] projection = {CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME};
        String selection = CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME + " >= ?";
        String[] selectionArgs = {Long.toString(oneMonthAgo)};
        String sortOrder = CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME + " ASC";
        return new CursorLoader(this, queryUri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int monthTotal = data.getCount();
        int weekTotal = 0;
        int dayTotal = 0;
        long weekInMillis = this.calculatePriorDateInMillis(WEEK); //Unix date in millis for one week prior to today
        long dayInMillis = this.calculatePriorDateInMillis(DAY); //Unix date in millis for one day prior to today

            while (data.moveToNext()) {
                long tempTimeValue = data.getLong(data.getColumnIndex(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME));
                if (weekInMillis <= tempTimeValue) { weekTotal++;}  //if coffee drunk after one week ago timestamp, increment
                if(dayInMillis <= tempTimeValue){dayTotal++;} //if coffee drunk after one day ago timestampe, increment
            }

        this.monthCupsCoffee.setText(Long.toString(monthTotal));
        this.weekCupsCoffee.setText(Long.toString(weekTotal));
        this.dayCupsCoffee.setText(Long.toString(dayTotal));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private long calculatePriorDateInMillis(int days) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DATE, c.get(Calendar.DATE) - days);
        return c.getTimeInMillis();
    }

}