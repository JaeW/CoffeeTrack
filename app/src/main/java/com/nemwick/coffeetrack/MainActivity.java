package com.nemwick.coffeetrack;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nemwick.coffeetrack.data.CoffeeContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 111;
    public static final String COFFEE_PREFERENCES = "preferenceFile"; //key to retrieve saved preferences file
    public static final String LAST_SAVED = "lastCoffeeSaved"; //key to save/retrieve most recent uri to saved preferences
    public static final String LAST_SAVED_TIME = "lastSavedTime"; //key to save/retrieve time last coffee consumed
    private Uri lastAddedCoffeeUri;
    private long lastAddedCoffeeTime;
    private RecyclerViewCursorAdapter adapter;
    private Snackbar snackbar;

    private View.OnClickListener mainClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    addNewCoffee();
                    snackbar.show();
                    break;
                case android.support.design.R.id.snackbar_action:
                    getContentResolver().delete(lastAddedCoffeeUri, null, null);
                    lastAddedCoffeeUri = getLastAddedCoffeeUri();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar setup
        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);

        //snackbar setup
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        snackbar = Snackbar.make(coordinatorLayout, R.string.snackbar_coffee_recorded, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_undo, this.mainClickListener);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        //fab setup
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mainClickListener);

        //recyclerview setup
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_coffee_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewCursorAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        //cursorloader initialization
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onStop() {
        saveLastAddedCoffee(lastAddedCoffeeUri, lastAddedCoffeeTime);
        super.onStop();
    }

    private void addNewCoffee() {
        lastAddedCoffeeTime = java.lang.System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LATITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LONGITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, lastAddedCoffeeTime);
        lastAddedCoffeeUri = getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME + " DESC";
        return new CursorLoader(this, CoffeeContract.CoffeeEntry.CONTENT_URI, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_coffee_stats:
                Intent intent = new Intent(this, CoffeeStatsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //store the uri for the coffee last added to the db
    private void saveLastAddedCoffee(Uri uri, Long lastTime) {
        if (lastAddedCoffeeUri != null) {
            SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(LAST_SAVED, uri.toString());
            editor.putLong(LAST_SAVED_TIME, lastTime);
            editor.apply();
        }
    }

    private Uri getLastAddedCoffeeUri() {
        SharedPreferences preferences = getSharedPreferences(COFFEE_PREFERENCES, Context.MODE_PRIVATE);
        String lastSavedUri = preferences.getString(LAST_SAVED, CoffeeContract.CoffeeEntry.CONTENT_URI + "0");
        return Uri.parse(lastSavedUri);
    }

}//end MainActivity
