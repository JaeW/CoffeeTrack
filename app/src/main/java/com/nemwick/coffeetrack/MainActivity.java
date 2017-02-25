package com.nemwick.coffeetrack;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nemwick.coffeetrack.data.CoffeeContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID = 111;
    private Uri lastAddedCoffeeUri; //TODO:  consider adding to saved preferences instead of variable
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerViewCursorAdapter adapter;
    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addNewCoffee();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);
        recyclerView = (RecyclerView) findViewById(R.id.rv_coffee_list);
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewCursorAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    private void addNewCoffee() {
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LATITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_LONGITUDE, 1);
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, java.lang.System.currentTimeMillis());
        lastAddedCoffeeUri = getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CoffeeContract.CoffeeEntry.CONTENT_URI, null, null, null, null);
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
                //TODO:  Launch coffee stats activity using explicit intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
