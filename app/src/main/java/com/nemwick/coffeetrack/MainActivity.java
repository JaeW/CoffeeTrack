package com.nemwick.coffeetrack;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nemwick.coffeetrack.data.CoffeeContract;

public class MainActivity extends AppCompatActivity {

    private Uri lastAddedCoffeeUri; //TODO:  consider adding to saved preferences instead of variable
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
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
        Cursor cursor = getContentResolver().query(CoffeeContract.CoffeeEntry.CONTENT_URI, null, null, null, null);
        RecyclerViewCursorAdapter recyclerViewCursorAdapter = new RecyclerViewCursorAdapter(cursor);
        recyclerView.setAdapter(recyclerViewCursorAdapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void addNewCoffee() {
        Long timeStamp = System.currentTimeMillis()/1000;
        ContentValues values = new ContentValues();
        values.put(CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME, timeStamp);
        lastAddedCoffeeUri = getContentResolver().insert(CoffeeContract.CoffeeEntry.CONTENT_URI, values);
        if(lastAddedCoffeeUri == null){
            Toast.makeText(this, "Coffee was not added to db", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Success!  Coffee added to db", Toast.LENGTH_SHORT).show();
        }
    }
}
