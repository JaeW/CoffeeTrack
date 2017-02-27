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

public class CoffeeStatsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView dayCupsCoffee;
    private TextView weekCupsCoffee;
    private TextView monthCupsCoffee;
    public static final int LOADER_ID = 222;

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
        Uri queryUri = CoffeeContract.CoffeeEntry.CONTENT_URI;
        String[] queryProjection = {CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME};
        //TODO;  Specify query to retrieve only last month's coffee records
        return new CursorLoader(this, queryUri, queryProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //TODO:  count cursor records of each type (day, week month) and populate TextViews

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
