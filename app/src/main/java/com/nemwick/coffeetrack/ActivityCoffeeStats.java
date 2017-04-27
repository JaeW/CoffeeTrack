package com.nemwick.coffeetrack;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nemwick.coffeetrack.data.CoffeeContract;
import com.nemwick.coffeetrack.utils.DateUtils;

public class ActivityCoffeeStats extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView dayCupsCoffee;
    private TextView weekCupsCoffee;
    private TextView monthCupsCoffee;
    public static final int LOADER_ID = 222;
    public static final int DAY = 1;
    public static final int WEEK = 7;
    public static final int MONTH = 30;
    public static final String TAG = "ActivityCoffeeStats:  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_stats);

        //toolbar setup
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_statistics));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load image
        ImageView imageView = (ImageView) findViewById(R.id.image_statistics);
        Glide.with(this).load(R.drawable.coffee_stats).into(imageView);


        //field - view binding
        dayCupsCoffee = (TextView) findViewById(R.id.cups_today);
        weekCupsCoffee = (TextView) findViewById(R.id.cups_week);
        monthCupsCoffee = (TextView) findViewById(R.id.cups_month);

        //cursorloader initialization
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Long oneMonthAgo = DateUtils.calculatePriorDateInMillis(MONTH);
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
        long weekInMillis = DateUtils.calculatePriorDateInMillis(WEEK); //Unix date in millis for one week prior to today
        long dayInMillis = DateUtils.calculatePriorDateInMillis(DAY); //Unix date in millis for one day prior to today
        //on screen rotation, 3 text view values must be recalculated
        //the Cursor, however, is stuck in the last position and does not move back to first
        // position if not explicitly directed to do so

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            long tempTimeValue = data.getLong(data.getColumnIndex(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME));
            if (DateUtils.convertSimpleDayFormat(tempTimeValue) == DateUtils.THIS_WEEK) {
                weekTotal++;
            }  //if coffee drunk < one week ago, increment
            if (DateUtils.convertSimpleDayFormat(tempTimeValue) == DateUtils.TODAY) {
                dayTotal++;
            } //if coffee drunk < one day ago, increment
        }

        //populate TextViews with values
        this.monthCupsCoffee.setText(Long.toString(monthTotal));
        this.weekCupsCoffee.setText(Long.toString(weekTotal));
        this.dayCupsCoffee.setText(Long.toString(dayTotal));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //intentionally left empty
    }

}
